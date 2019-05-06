package com.sergey.zhuravlev.auctionserver.database.repository;

import com.sergey.zhuravlev.auctionserver.database.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, Long>, QuerydslPredicateExecutor<Bid> {

    @Query("SELECT bid FROM Bid bid WHERE bid.lot.id = :id AND bid.amount = (SELECT MAX(bid.amount) FROM Bid bid WHERE bid.lot.id = :id AND bid.status = 'BROKEN')")
    Bid getBidByLotIdAndMaxSize(@Param("id") Long id);

    Bid getBidById(Long id);

}