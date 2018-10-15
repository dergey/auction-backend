package com.sergey.zhuravlev.auctionserver.service;

import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.service.notification.NotificationService;
import com.sergey.zhuravlev.auctionserver.service.notification.type.NotificationLotExpired;
import com.sergey.zhuravlev.auctionserver.service.notification.type.NotificationLotPurchased;
import com.sergey.zhuravlev.auctionserver.service.notification.type.NotificationLotSold;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Log
@Service
public class WatcherExpirationLotsService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private LotsThread thread;

    @Autowired
    public WatcherExpirationLotsService(LotRepository lotRepository, BidRepository bidRepository, NotificationService notificationService) {
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
    }

    @PostConstruct
    private void init(){
        thread = new LotsThread();
        try {
            doFollowing(foundNearestLot());
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

    private void doFollowing(Lot lot) {
        thread.interrupt();
        thread = new LotsThread();
        thread.setNearestLot(lot);
        thread.start();
    }

    public void follow(Lot lot) {
        if (thread.nearestLotIsNull() ||
                lot.getExpirationDate().getTime() < thread.getNearestLotTime()) {
            this.doFollowing(lot);
        }
    }

    public void unfollowById(Long id) {
        if (thread.getNearestLot().getId().equals(id)) {
            doFollowing(foundNearestLot());
        }
    }

    @Getter
    @Setter
    class LotsThread extends Thread {

        private Lot nearestLot;

        @Override
        public void run() {
            log.info("Thread " + getName() + " started, nearest lot [" + nearestLot.getId() + "] " + nearestLot.getTitle() + ", expires at "
                    + getHoursMinute(nearestLot.getExpirationDate().getTime() - System.currentTimeMillis()));
            try {
                Thread.sleep(nearestLot.getExpirationDate().getTime() - System.currentTimeMillis());
                Bid bid = bidRepository.getBetByLotIdAndMaxSize(nearestLot.getId());
                if (bid != null) {
                    // Уведомление покупателю
                    notificationService.send(new NotificationLotPurchased(bid.getBuyer(), nearestLot));
                    // Уведомление продавцу
                    notificationService.send(new NotificationLotSold(nearestLot));
                    nearestLot.setStatus(Status.SOLD);
                    bidRepository.deleteAllBetsInLots(nearestLot.getId());
                } else {
                    //Уведомление продавцу
                    nearestLot.setStatus(Status.UNSOLD);
                    lotRepository.saveAndFlush(nearestLot);
                    notificationService.send(new NotificationLotExpired(nearestLot));
                    log.info("Lot " + nearestLot.getTitle() + " not sold");
                }
                lotRepository.save(nearestLot);
            } catch (InterruptedException ignored) {
                log.info("Thread stopped.");
            }
            log.info("Thread destroyed.");
        }

        private String getHoursMinute(Long millis) {
            int minute = Math.round(millis / 60000);
            int hours = Math.round(minute / 60);
            minute = minute - hours * 60;
            return String.format("%s h. %s m.", hours, minute);
        }

        boolean nearestLotIsNull() {
            return nearestLot == null;
        }

        Long getNearestLotTime() {
            return nearestLot.getExpirationDate().getTime();
        }

    }

}
