package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;

    @Autowired
    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @Transactional
    public Bid create(Lot lot, Double size, User owner) {
        if (!lot.getStatus().equals(Status.SALE)) throw new RuntimeException("STUB");
        Bid currentBid = getLastBid(lot.getId());
        if (currentBid != null && size <= currentBid.getSize()) throw new RuntimeException("STUB");
        Bid bid = new Bid(null, new Date(System.currentTimeMillis()), size, lot, owner);
        bidRepository.save(bid);
        return bid;
        // TODO Уведомление предыдущему владельцу лоту
        //log.debug("Ставка  : " + currentBid);
        //TODO move to aspect
//        if (currentBid != null && !currentBid.getBuyer().getNotificationToken().isEmpty()) {
//            // log.debug("SEND MESSAGE : buyer - " + currentBid.getBuyer().getUsername() + " new bid size - " + bid.getSize() + " lot " + lot);
//            notificationService.send(new NotificationBetBroken(currentBid.getBuyer(), bid, lot));
//        }
    }

    @Transactional
    public void delete(Long id) {
        bidRepository.deleteById(id);
    }

    @Transactional
    public List<Bid> getAllBetByLotId(Long lotId) {
        return bidRepository.getByLot_Id(lotId);
    }

    @Transactional
    public Bid getLastBid(Long id) {
        return bidRepository.getBetByLotIdAndMaxSize(id);
    }

}
