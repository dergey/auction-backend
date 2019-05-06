package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.core.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.database.entity.*;
import com.sergey.zhuravlev.auctionserver.database.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.database.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.database.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.database.repository.ImageRepository;
import com.sergey.zhuravlev.auctionserver.database.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Log
@Service
@RequiredArgsConstructor
public class LotService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final ImageRepository imageRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Lot getLot(Long lotId) {
        Optional<Lot> lot = lotRepository.findById(lotId);
        if (!lot.isPresent())
            throw new NotFoundException(String.format("Lot with id %s not found", lotId));
        return lot.get();
    }

    @Transactional(readOnly = true)
    public Page<Lot> list(LotPredicateBuilder lotPredicateBuilder, Pageable pageable) {
        return lotRepository.findAll(lotPredicateBuilder.build(), pageable);
    }

    @Transactional
    public Lot createLot(Account account, Category category, String title, String description,
                         Collection<String> imageNames, Date expiresAt, Long startingAmount, Long auctionStep,
                         Currency currency) {
        Date now = new Date();

        if (expiresAt.before(now)) throw new RuntimeException("Expires date will be after create date");
        Collection<Image> images = imageRepository.findAllByNameIn(imageNames);

        Lot lot = new Lot(null,
                title,
                description,
                images,
                now,
                now,
                null,
                expiresAt,
                startingAmount,
                currency,
                auctionStep,
                null,
                LotStatus.ACTIVE,
                account,
                category);
        lotRepository.save(lot);
        return lot;
    }

    @Transactional
    public Lot updateLot(Lot lot, Account account, Category category, String title, String description,
                         Collection<String> imageNames, Date expiresAt, Long startingAmount, Long auctionStep, Currency currency) {
        Date now = new Date();
        lot = lotRepository.findByIdAndOwner(lot.getId(), account).orElseThrow(() -> new NotFoundException("Lot not found"));
        //Обновлятся могут только отмененые и не проданые!
        if (expiresAt.equals(lot.getExpiresAt()) ||
                expiresAt.after(lot.getExpiresAt())) throw new BadRequestException("Not possible update expires date");
        if (expiresAt.before(now)) throw new BadRequestException("Expires date will be after create date");

        Collection<Image> images = imageRepository.findAllByNameIn(imageNames);
        lot.setTitle(title);
        lot.setDescription(description);
        lot.setImages(images);
        lot.setUpdateAt(now);
        lot.setExpiresAt(expiresAt);
        lot.setStartingAmount(startingAmount);
        lot.setCurrency(currency);
        lot.setAuctionStep(auctionStep);
        lot.setStatus(LotStatus.ACTIVE);
        lot.setCategory(category);
        return lot;
    }

    @Transactional
    public Lot cancelLot(Lot lot, Account account) {
        Date now = new Date();
        lot = lotRepository.findByIdAndOwner(lot.getId(), account).orElseThrow(() -> new NotFoundException("Lot not found"));
        if (lot.getStatus() != LotStatus.ACTIVE) throw new BadRequestException("Possible to cancel only active lots");
        lot.setStatus(LotStatus.CANCELED);
        lot.setUpdateAt(now);
        return lot;
    }

    @Transactional
    public Lot completeLot(Lot lot) {
        lot = lotRepository.findById(lot.getId()).orElseThrow(() -> new RuntimeException("Can't complete lot! Not found!"));
        Date now = new Date();
        Bid bid = lot.getCurrentBid();
        if (bid != null) {
            lot.setStatus(LotStatus.SOLD);
            notificationService.createNotificationLotPurchased(lot);
            notificationService.createNotificationLotSold(lot);
        } else {
            lot.setStatus(LotStatus.UNSOLD);
            lotRepository.save(lot);
            notificationService.createNotificationLotExpired(lot);
            log.info(String.format("Lot %s not sold", lot.getTitle()));
        }
        lot.setUpdateAt(now);
        lot.setExpiresAt(now);
        return lotRepository.save(lot);
    }

    @PostConstruct
    @Transactional
    public void autocompleteLots() {
        long time = System.currentTimeMillis();
        List<Lot> lots = lotRepository.getIncompleteLots();
        for (Lot lot : lots) {
            Bid lastBid = bidRepository.getBidByLotIdAndMaxSize(lot.getId());
            if (lastBid != null) {
                lot.setStatus(LotStatus.SOLD);
                Bid bid = lot.getCurrentBid();
                bid.setStatus(BidStatus.SUCCESSFUL);
                lotRepository.save(lot);
            } else {
                lot.setStatus(LotStatus.UNSOLD);
                lotRepository.save(lot);
            }
        }
        log.info(String.format("%s lots were completed in %s ms", lots.size(), System.currentTimeMillis() - time));
    }

}
