package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dao.BidDao;
import com.sergey.zhuravlev.auctionserver.model.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetServiceImpl implements BetService {

    @Autowired
    private BidDao bidDao;

    @Override
    public void make(Bid bid) {
        bidDao.save(bid);
    }

    @Override
    public void remove(Long id) {
        bidDao.delete(id);
    }

    @Override
    public List<Bid> getAllBetByLotId(Long lotId) {
        return bidDao.getByLot_Id(lotId);
    }

    @Override
    public Bid getBetByLotIdAndMaxSize(Long id) {
        return bidDao.getBetByLotIdAndMaxSize(id);
    }

}
