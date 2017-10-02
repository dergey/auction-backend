package com.sergey.zhuravlev.auctionserver.thread;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.dao.LotDao;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.model.Bid;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.notification.NotificationLotExpired;
import com.sergey.zhuravlev.auctionserver.notification.NotificationLotPurchased;
import com.sergey.zhuravlev.auctionserver.notification.NotificationLotSold;
import com.sergey.zhuravlev.auctionserver.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LotsThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(LotsThread.class);

    private NotificationService notificationService;
    private LotDao lotDao;
    private BidDao bidDao;
    private Lot nearestLot;

    @Override
    public void run() {
        logger.debug("Поток " + getName() + " запущен, ближайщий лот [" + nearestLot.getId() + "] " + nearestLot.getTitle() + ", закончится через "
                + getHoursMinuts(nearestLot.getExpirationDate().getTime() - System.currentTimeMillis()));
        try {
            Thread.sleep(nearestLot.getExpirationDate().getTime() - System.currentTimeMillis());
            Bid bid = bidDao.getBetByLotIdAndMaxSize(nearestLot.getId());
            if (bid != null) {
                // Уведомление покупателю
                notificationService.send(new NotificationLotPurchased(bid.getBuyer(), nearestLot));
                // Уведомление продавцу
                notificationService.send(new NotificationLotSold(nearestLot));
                nearestLot.setStatus(Status.Sold);
                bidDao.deleteAllBetsInLots(nearestLot.getId());
            } else {
                //Уведомление продавцу
                nearestLot.setStatus(Status.Unsold);
                lotDao.saveAndFlush(nearestLot);
                notificationService.send(new NotificationLotExpired(nearestLot));
                logger.debug("Лот " + nearestLot.getTitle() + " не продан");
            }
            lotDao.save(nearestLot);
        } catch (InterruptedException ignored) {
            logger.debug("Поток остановлен.");
        }
        logger.debug("Поток уничтожен.");
    }

    private String getHoursMinuts(Long millis){
        int minuts = Math.round(millis / 60000);
        int hours = Math.round(minuts / 60);
        minuts = minuts - hours * 60;
        return hours+"ч. "+minuts + "мин.";
    }

    protected void setLotDao(LotDao lotDao){
        this.lotDao = lotDao;
    }

    protected void setBidDao(BidDao bidDao) {
        this.bidDao = bidDao;
    }

    public Lot getNearestLot() {
        return nearestLot;
    }

    public void setNearestLot(Lot nearestLot) {
        this.nearestLot = nearestLot;
    }

    public boolean nearestLotIsNull(){
        return nearestLot == null;
    }

    public Long getCurrentNearestLot() {
        return nearestLot.getExpirationDate().getTime();
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}