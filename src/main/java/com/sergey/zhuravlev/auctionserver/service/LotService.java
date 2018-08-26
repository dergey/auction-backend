package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Category;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
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

    public Lot get(Long id) {
        Optional<Lot> lot = lotRepository.findById(id);
        if (!lot.isPresent())
            throw new NotFoundException(String.format("Lot with id %s not found", id));
        return lot.get();
    }

    public List<Lot> getUserLots(Long userId) {
        return lotRepository.getByOwner_Id(userId);
    }

    public List<Lot> getByOwnerIDAndStatus(Long ownerID, Status status) {
        return lotRepository.getByOwnerIdAndStatus(ownerID, status);
    }

    public List<Lot> getLotsByBuyerId(Long id) {
        return lotRepository.getLotsByBuyer(id);
    }

    @Transactional
    public List getLots(Long categoryID, Long ownerID, String query, Integer offset){
        String q = "select l from Lot l";

        if (offset == null) offset = 0;

        if (categoryID != null) q = "select l from Lot l where l.category.id =" + categoryID; else
        if (ownerID != null) q = "select l from Lot l where l.owner.id = " + ownerID; else
        if (query != null) q = "select l from Lot l where l.title like %" + query + "%";

        SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);

        return sf.getCurrentSession().createQuery(q)
                .setFirstResult(offset)
                .setMaxResults(5)
                .list();
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

    public List<Lot> search(String query) {
        return lotRepository.getLotsByTitleLike(query);
    }

    public List<Lot> getPurchasedLots(Long id) {
        return lotRepository.getPurchasedLots(id);
    }

    public List<Lot> getRandom() {
        return lotRepository.getRandom(5);
    }

}
