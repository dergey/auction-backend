package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> getByLotId(Long id);

    @Query("SELECT bid FROM Bid bid WHERE bid.lot = :lot AND bid.status = 'ACTUAL'")
    Bid getBidByLotAndStatusActual(Lot lot);

    @Query("SELECT bid FROM Bid bid WHERE bid.lot.id = :id AND bid.amount = (SELECT MAX(bid.amount) FROM Bid bid WHERE bid.lot.id = :id AND bid.status = 'BROKEN')")
    Bid getBidByLotIdAndMaxSize(@Param("id") Long id);

    Bid getBidById(Long id);

}