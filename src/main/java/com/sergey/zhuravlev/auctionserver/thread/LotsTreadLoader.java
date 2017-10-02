package com.sergey.zhuravlev.auctionserver.thread;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.dao.LotDao;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LotsTreadLoader {

    private static final Logger logger = LoggerFactory.getLogger(LotsTreadLoader.class);

    @Autowired
    private LotDao lotDao;

    @Autowired
    private BidDao bidDao;

    @Autowired
    NotificationService notificationService;

    private LotsThread thread;

    @PostConstruct
    private void init(){
        thread = new LotsThread();
        thread.setLotDao(lotDao);
        thread.setBidDao(bidDao);
        thread.setNotificationService(notificationService);
        try {
            foundNearestLot();
            thread.start();
        } catch (Exception e) {
            logger.error("Found Nearest Lot Error: " + e.getMessage());
        }
    }

    public void foundNearestLot() throws Exception {
        if (thread.isAlive()) thread.interrupt();
        Lot lot = lotDao.getNearestExpirationDate();
        if (lot != null) thread.setNearestLot(lotDao.getNearestExpirationDate());
            else throw new Exception("Not found");
    }

    public LotsThread getTread() {
        return thread;
    }

    public void start(Lot lot) {
        thread.interrupt();
        thread = new LotsThread();
        thread.setLotDao(lotDao);
        thread.setBidDao(bidDao);
        thread.setNotificationService(notificationService);
        thread.setNearestLot(lot);
        thread.start();
    }
}
