package com.sergey.zhuravlev.auctionserver.repository;


import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long>, QuerydslPredicateExecutor<Lot> {

    Lot getById(Long id);

    @Override
    Optional<Lot> findOne(Predicate predicate);

    @Override
    Iterable<Lot> findAll(Predicate predicate);

    @Query("SELECT l FROM Lot l WHERE l.status = 0 AND l.expirationDate < CURRENT_TIMESTAMP")
    List<Lot> getIncompleteLots();

    @Query(value = "SELECT * FROM lots WHERE lots.status = 0 ORDER BY RAND() LIMIT ?", nativeQuery = true)
    List<Lot> getRandom(@Param("rand") int i);
}
