package com.sergey.zhuravlev.auctionserver.repository;


import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Lot getById(Long id);

    List<Lot> getByOwner_Id(Long id);

    List<Lot> getByOwnerIdAndStatus(Long id, Status status);

    @Query("SELECT l FROM Lot l WHERE l.expirationDate = (SELECT MIN(l.expirationDate) FROM Lot l WHERE l.status = 0 AND l.expirationDate > CURRENT_TIMESTAMP)")
    Lot getNearestExpirationDate();

    @Query("SELECT DISTINCT b.lot FROM Bid b WHERE b.buyer.id = :id AND b.lot.status = 0")
    List<Lot> getLotsByBuyer(@Param("id")Long id);

    @Query("Select l from Lot l where l.title like %:query%")
    List<Lot> getLotsByTitleLike(@Param("query")String query);

    @Query(value = "SELECT l.* FROM lots l INNER JOIN bids ON l.id = bids.lot_id WHERE l.status = 1 AND bids.user_id = ?1", nativeQuery = true)
    List<Lot> getPurchasedLots(@Param("buyerId")Long buyerID);

    @Query("SELECT l FROM Lot l WHERE l.status = 0 AND l.expirationDate < CURRENT_TIMESTAMP")
    List<Lot> getIncompleteLots();

    @Query(value = "SELECT * FROM lots WHERE lots.status = 0 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Lot> getRandom(@Param("rand") int i);
}
