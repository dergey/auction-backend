package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.model.Bid;

import java.util.List;

public interface BetService {

    void make(Bid bid);
    void remove(Long id);
    List<Bid> getAllBetByLotId(Long lotId);
    Bid getBetByLotIdAndMaxSize(Long id);

}
