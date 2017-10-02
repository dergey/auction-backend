package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;

import java.util.List;

public interface LotService {

    List<Lot> getAll();

    Lot getByID(Long id);
    List<Lot> getByCategoryID(Long categoryID);

    List<Lot> getByOwner(User user);

    List<Lot> getByOwnerID(Long categoryID);

    List<Lot> getByOwnerIDAndStatus(Long ownerID, Integer status);

    List<Lot> getLotsByBuyerId(Long id);

    List<Lot> getLots(Long categoryID, Long ownerID, String query, Integer offset);

    Lot getNearestExpirationDate();

    void save(Lot lot);

    void remove(Long id);

    List<Lot> search(String query);

    List<Lot> getPurchasedLots(Long id);

    List<Lot> getRandom();
}
