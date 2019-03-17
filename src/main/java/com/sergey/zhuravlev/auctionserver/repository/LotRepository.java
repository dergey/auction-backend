package com.sergey.zhuravlev.auctionserver.repository;


import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long>, QuerydslPredicateExecutor<Lot> {

    Lot getLotById(Long id);

    @Override
    Optional<Lot> findOne(Predicate predicate);

    @Override
    Collection<Lot> findAll(Predicate predicate);

    @Query("SELECT l FROM Lot l WHERE l.status = 'ACTIVE' AND l.expiresAt < CURRENT_TIMESTAMP")
    List<Lot> getIncompleteLots();

}
