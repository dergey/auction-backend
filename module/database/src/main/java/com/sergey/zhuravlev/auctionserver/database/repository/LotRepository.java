package com.sergey.zhuravlev.auctionserver.database.repository;


import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long>, QuerydslPredicateExecutor<Lot> {

    Optional<Lot> findById(Long id);

    Optional<Lot> findByIdAndOwner(Long id, Account owner);

    Optional<Lot> findFirstByExpiresAtAfterOrderByExpiresAtAsc(Date date);

    @Override
    Optional<Lot> findOne(Predicate predicate);

    @Override
    Collection<Lot> findAll(Predicate predicate);

    @Query("SELECT l FROM Lot l WHERE l.status = 'ACTIVE' AND l.expiresAt < CURRENT_TIMESTAMP")
    List<Lot> getIncompleteLots();

}
