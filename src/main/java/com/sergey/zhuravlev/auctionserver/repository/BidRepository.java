package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> getByLot_Id(Long id);

    @Query("SELECT b FROM Bid b WHERE b.lot.id = :id AND b.size = (SELECT MAX(b.size) FROM Bid b WHERE b.lot.id = :id)")
    Bid getBetByLotIdAndMaxSize(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE t1.* FROM bids t1 INNER JOIN (SELECT lot_id, MAX(size) sizeMax FROM bids WHERE lot_id = ?1) t2 ON t1.lot_id = t2.lot_id  WHERE t1.size <> t2.sizeMax", nativeQuery = true)
    @Transactional
    void deleteAllBetsInLots(@Param("id") Long id);

}