package com.sergey.zhuravlev.auctionserver.service;

import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Log
@Service
@RequiredArgsConstructor
public class WatcherExpirationLotsService {

    private final LotService lotService;
    private final LotRepository lotRepository;

    private LotsThread thread;

    @PostConstruct
    private void init(){
        try {
            followImpl(foundNearestLot());
        } catch (RuntimeException e) {
            log.info(String.format("Unable to start tracking nearest lot. %s", e));
        }
    }

    private Lot foundNearestLot() {
        if (thread.isAlive()) thread.interrupt();
        Predicate lotPredicate = new LotPredicateBuilder().withNearestExpirationDate().build();
        Optional<Lot> lotOptional = lotRepository.findOne(lotPredicate);
        if (!lotOptional.isPresent())
            throw new RuntimeException("Not found nearest lots");
        return lotOptional.get();
    }

    private void followImpl(Lot lot) {
        thread.interrupt();
        thread = new LotsThread(lot);
        thread.start();
    }

    @Transactional
    public void follow(Lot lot) {
        if (thread.nearestLotIsNull() ||
                lot.getExpiresAt().getTime() < thread.getNearestLotTime()) {
            this.followImpl(lot);
        }
    }

    public void unfollowById(Long id) {
        if (thread.getNearestLot().getId().equals(id)) {
            followImpl(foundNearestLot());
        }
    }

    @Getter
    @RequiredArgsConstructor
    class LotsThread extends Thread {

        private final Lot nearestLot;

        @Override
        public void run() {
            Long sleepTime = nearestLot.getExpiresAt().getTime() - System.currentTimeMillis();
            log.info("Thread " + getName() + " started, nearest lot [" + nearestLot.getId() + "] " + nearestLot.getTitle() + ", expires at "
                    + formatMillisToHoursMinute(sleepTime));
            try {
                Thread.sleep(nearestLot.getExpiresAt().getTime() - System.currentTimeMillis());
                lotService.completeLot(nearestLot);
            } catch (InterruptedException ignored) {
                log.info("Thread stopped.");
            }
            log.info("Thread destroyed.");
        }

        private String formatMillisToHoursMinute(Long millis) {
            int minute = (int) (millis / 60000);
            int hours = minute / 60;
            minute = minute - hours * 60;
            return String.format("%s h. %s m.", hours, minute);
        }

        boolean nearestLotIsNull() {
            return nearestLot == null;
        }

        Long getNearestLotTime() {
            return nearestLot.getExpiresAt().getTime();
        }

    }

}
