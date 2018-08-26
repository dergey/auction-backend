package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Log
@Component
public class AwakenedService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;

    @Autowired
    public AwakenedService(LotRepository lotRepository, BidRepository bidRepository) {
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
    }

    @PostConstruct
    public void autocompleteLots() {
        Long time = System.currentTimeMillis();
        List<Lot> lots = lotRepository.getIncompleteLots();
        for (Lot lot:lots) {
            Bid lastBid = bidRepository.getBetByLotIdAndMaxSize(lot.getId());
            if (lastBid != null) {
                lot.setStatus(Status.SOLD);
                bidRepository.deleteAllBetsInLots(lot.getId());
                lotRepository.save(lot);
            } else {
                lot.setStatus(Status.UNSOLD);
                lotRepository.save(lot);
            }

        }
        log.info(String.format("%s lots were completed in %s ms", lots.size(), System.currentTimeMillis() - time));
    }

}
