package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.core.service.LotService;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.database.repository.LotRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class WatcherExpirationLotsService {

    private final LotService lotService;
    private final LotRepository lotRepository;

    private WatcherExpirationLotsThread thread;

    @PostConstruct
    private void init() {
        followNearestLot();
    }

    private Lot foundNearestLot() {
        if (thread != null && thread.isAlive()) thread.interrupt();
        return lotRepository
                .findFirstByExpiresAtAfterOrderByExpiresAtAsc(new Date())
                .orElseThrow(() -> new NotFoundException("Not found nearest lots"));
    }

    private void followImpl(Lot lot) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        log.info("Follow the lot {}.", lot);
        thread = new WatcherExpirationLotsThread(lot);
        thread.start();
    }

    @Transactional
    public void follow(Lot lot) {
        if (thread == null || thread.nearestLotIsNull() || lot.getExpiresAt().getTime() < thread.getNearestLotTime()) {
            this.followImpl(lot);
        }
    }

    @Transactional
    public void followNearestLot() {
        try {
            followImpl(foundNearestLot());
        } catch (NotFoundException e) {
            log.info("Unable to start tracking nearest lot. Nearest lot not found.");
        } catch (Exception e) {
            log.warn("Unable to start tracking nearest lot.", e);
        }
    }

    public void unfollow(Lot lot) {
        if (thread.getNearestLot().equals(lot)) {
            followImpl(foundNearestLot());
        }
    }

    @Getter
    @RequiredArgsConstructor
    class WatcherExpirationLotsThread extends Thread {

        private final Lot nearestLot;

        @Override
        public void run() {
            Long sleepTime = nearestLot.getExpiresAt().getTime() - System.currentTimeMillis();
            log.info("Watcher expiration lots thread {} started, nearest lot {} [{}], expires in {}.",
                    this.getName(), nearestLot.getId(), nearestLot.getTitle(), formatMillisToHoursMinute(sleepTime));
            try {
                Thread.sleep(nearestLot.getExpiresAt().getTime() - System.currentTimeMillis());
                lotService.completeLot(nearestLot);
            } catch (InterruptedException ignored) {
                log.info("Watcher expiration lots thread {} stopped.", this.getName());
            }
            log.info("Watcher expiration lots thread {} exit.", this.getName());
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
