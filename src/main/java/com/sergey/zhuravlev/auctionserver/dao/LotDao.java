package com.sergey.zhuravlev.auctionserver.dao;


import com.sergey.zhuravlev.auctionserver.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LotDao extends JpaRepository<Lot, Long> {

    List<Lot> getByCategory_Id(Long id);

    List<Lot> getByOwner_Id(Long id);

    List<Lot> getByOwnerIdAndStatus(Long id, Integer status);

    @Query("SELECT l FROM Lot l WHERE l.expirationDate = (SELECT MIN(l.expirationDate) FROM Lot l WHERE l.status = 0 AND l.expirationDate > CURRENT_TIMESTAMP)")
    Lot getNearestExpirationDate();

    @Query("SELECT DISTINCT b.lot FROM Bid b WHERE b.buyer.id = :id AND b.lot.status = 0")
    List<Lot> getLotsByBuyer(@Param("id")Long id);

    @Query("Select l from Lot l where l.title like %:query%")
    List<Lot> getLotsByTitleLike(@Param("query")String query);

    @Query(value = "select l.* from lots l inner join bids on l.id = bids.lot_id where l.status = 1 and bids.user_id = ?1", nativeQuery = true)
    List<Lot> getPurchasedLots(@Param("buyerId")Long buyerID);

    @Query("SELECT l FROM Lot l WHERE l.status = 0 AND l.expirationDate < CURRENT_TIMESTAMP")
    List<Lot> getUncompliteLots();

    @Query(value = "SELECT * FROM lots WHERE lots.status = 0 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Lot> getRandom(@Param("rand") int i);

}
