package com.sergey.zhuravlev.auctionserver.service;

import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.common.SimplePage;
import com.sergey.zhuravlev.auctionserver.entity.Category;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.sergey.zhuravlev.auctionserver.exception.SecurityException;

@Log
@Service
public class LotService {

    private final WatcherExpirationLotsService watcherExpirationLotsService;
    private final EntityManagerFactory entityManagerFactory;
    private final LotRepository lotRepository;

    @Autowired
    public LotService(WatcherExpirationLotsService watcherExpirationLotsService,
                      EntityManagerFactory entityManagerFactory,
                      LotRepository lotRepository) {
        this.watcherExpirationLotsService = watcherExpirationLotsService;
        this.entityManagerFactory = entityManagerFactory;
        this.lotRepository = lotRepository;
    }

    @Transactional
    public Lot get(Long id) {
        Optional<Lot> lot = lotRepository.findById(id);
        if (!lot.isPresent())
            throw new NotFoundException(String.format("Lot with id %s not found", id));
        return lot.get();
    }

    @Transactional
    public Lot create(String title, String description, String image,
                      Date expirationDate, Double startingPrice, Double auctionStep,
                      User owner, Category category) {
        Lot lot = new Lot(null, title, description, image, expirationDate,
                startingPrice,  auctionStep, Status.SALE, owner, category);
        lotRepository.save(lot);
        watcherExpirationLotsService.follow(lot);
        return lot;
    }

    @Transactional
    public Lot update(Long id, String title, String description, String image,
                      Date expirationDate, Double startingPrice, Double auctionStep,
                      User owner, Category category) {
        Optional<Lot> lotOptional = lotRepository.findById(id);
        if (!lotOptional.isPresent()) throw new NotFoundException(String.format("Lot with id %s not found", id));
        Lot lot = lotOptional.get();
        if (!lot.getOwner().equals(owner)) throw new SecurityException("Insufficient permissions to update");
        lot.setTitle(title);
        lot.setDescription(description);
        lot.setImage(image);
        lot.setExpirationDate(expirationDate);
        lot.setStartingPrice(startingPrice);
        lot.setAuctionStep(auctionStep);
        lot.setCategory(category);
        lotRepository.save(lot);
        watcherExpirationLotsService.follow(lot); //todo move to aspect
        return lot;
    }

    @Transactional
    public void delete(Long id) {
        Lot lot = lotRepository.getById(id);
        User user = SecurityService.getAuthenticationUser();
        if (!user.getId().equals(lot.getOwner().getId()))
            throw new SecurityException("Insufficient permissions to delete");
        lotRepository.deleteById(id);
        watcherExpirationLotsService.unfollowById(id); //todo move to aspect
    }

    @Transactional
    public Iterable<Lot> getLots(Status status, String titleLike, Long ownerId, Long categoryId,
                                 Integer pageNumber, Integer pageSize) {
        Predicate lotPredicate = new LotPredicateBuilder()
                .withStatus(status)
                .withTitleLike(titleLike)
                .withOwnerId(ownerId)
                .withCategoryId(categoryId)
                .build();
        SimplePage page = new SimplePage(pageSize, pageNumber);
        return lotRepository.findAll(lotPredicate, page);
    }

    @Transactional
    public Iterable<Lot> getLotsByBuyer(User buyer) {
        Predicate lotPredicate = new LotPredicateBuilder()
                .withStatus(Status.SALE)
                .withBuyer(buyer)
                .build();
        return lotRepository.findAll(lotPredicate);
    }

    @Transactional
    public Iterable<Lot> getUserPurchasedLots(User user) {
        Predicate lotPredicate = new LotPredicateBuilder()
                .withStatus(Status.SOLD)
                .withBuyer(user)
                .build();
        return lotRepository.findAll(lotPredicate);
    }

    public List<Lot> getRecommendLots() {
        return lotRepository.getRandom(5);
    }
}
