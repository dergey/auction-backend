package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.dao.LotDao;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.model.Bid;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Utils {

    @Autowired
    LotDao lotDao;

    @Autowired
    BidDao bidDao;

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    @PostConstruct
    public void autoCompliteAllLot() {
        Long time = System.currentTimeMillis();
        List<Lot> lots = lotDao.getUncompliteLots();
        for (Lot lot:lots) {
            Bid lastBid = bidDao.getBetByLotIdAndMaxSize(lot.getId());
            if (lastBid != null) {
                lot.setStatus(Status.Sold);
                bidDao.deleteAllBetsInLots(lot.getId());
                lotDao.save(lot);
            } else {
                lot.setStatus(Status.Unsold);
                lotDao.save(lot);
            }

        }
        logger.debug("Было завершено " + lots.size() + " лотов за " + (System.currentTimeMillis() - time) + " мс" );
    }

}
