package com.sergey.zhuravlev.auctionserver.service;

import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.dto.RequestLotDto;
import com.sergey.zhuravlev.auctionserver.entity.*;
import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.enums.NotificationType;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.exception.SecurityException;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.repository.CategoryRepository;
import com.sergey.zhuravlev.auctionserver.repository.ImageRepository;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.*;
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
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Lot getLot(Long lotId) {
        Optional<Lot> lot = lotRepository.findById(lotId);
        if (!lot.isPresent())
            throw new NotFoundException(String.format("Lot with id %s not found", lotId));
        return lot.get();
    }

    @Transactional(readOnly = true)
    public Collection<Lot> list(LotStatus lotStatus, String titleLike, Long ownerId, Long categoryId, Integer pageNumber, Integer pageSize) {
        Predicate lotPredicate = new LotPredicateBuilder()
                .withStatus(lotStatus)
                .withTitleLike(titleLike)
                .withOwnerId(ownerId)
                .withCategoryId(categoryId)
                .build();

        Page<Lot> page = lotRepository.findAll(lotPredicate, PageRequest.of(pageNumber, pageSize,
                Sort.Direction.ASC, "firstName"));
        return page.getContent();
    }

    @Transactional
    public Lot createLot(RequestLotDto lotDto, User user) {
        Date now = new Date();
        Optional<Category> categoryOptional = categoryRepository.findById(lotDto.getCategoryId());
        if (!categoryOptional.isPresent()) throw new NotFoundException(String.format("Category with id %s not found", lotDto.getCategoryId()));
        if (lotDto.getExpiresAt().before(now)) throw new RuntimeException("Expires date will be after create date");
        Collection<Image> images = imageRepository.findAllByNameIn(lotDto.getImages());

        Lot lot = new Lot(null,
                lotDto.getTitle(),
                lotDto.getDescription(),
                images,
                now,
                now,
                null,
                lotDto.getExpiresAt(),
                lotDto.getStartingAmount(),
                Currency.getInstance(lotDto.getCurrencyCode()),
                lotDto.getAuctionStep(),
                LotStatus.ACTIVE,
                user,
                categoryOptional.get());
        lotRepository.save(lot);
        return lot;
    }

    @Transactional
    public Lot updateLot(Lot lot, RequestLotDto lotDto, User user) {
        Date now = new Date();
        //TODO move Before
        if (!lot.getOwner().getId().equals(user.getId())) throw new SecurityException("Insufficient permissions to update");
        lot = lotRepository.getLotById(lot.getId());
        //Обновлятся могут только отмененые и не проданые!
        if (lotDto.getExpiresAt().equals(lot.getExpiresAt()) ||
                lotDto.getExpiresAt().after(lot.getExpiresAt())) throw new RuntimeException("Not possible update expires date");
        if (lotDto.getExpiresAt().before(now)) throw new RuntimeException("Expires date will be after create date");
        Optional<Category> categoryOptional = categoryRepository.findById(lotDto.getCategoryId());
        if (!categoryOptional.isPresent()) throw new NotFoundException(String.format("Category with id %s not found", lotDto.getCategoryId()));
        Collection<Image> images = imageRepository.findAllByNameIn(lotDto.getImages());
        lot.setTitle(lotDto.getTitle());
        lot.setDescription(lotDto.getDescription());
        lot.setImages(images);
        lot.setUpdateAt(now);
        lot.setExpiresAt(lotDto.getExpiresAt());
        lot.setStartingAmount(lotDto.getStartingAmount());
        lot.setCurrency(Currency.getInstance(lotDto.getCurrencyCode()));
        lot.setAuctionStep(lotDto.getAuctionStep());
        lot.setStatus(LotStatus.ACTIVE);
        lot.setCurrency(Currency.getInstance(lotDto.getCurrencyCode()));
        lot.setCategory(categoryOptional.get());
        return lot;
    }

    @Transactional
    public Lot cancelLot(Lot lot, User user) {
        Date now = new Date();
        if (!lot.getOwner().getId().equals(user.getId())) throw new SecurityException("Insufficient permissions to update");
        lot = lotRepository.getLotById(lot.getId());
        if (lot.getStatus() != LotStatus.ACTIVE) throw new RuntimeException("Possible to cancel only active lots");
        lot.setStatus(LotStatus.CANCELED);
        lot.setUpdateAt(now);
        return lot;
    }

    @Transactional
    public Lot completeLot(Lot lot) {
        Bid bid = bidRepository.getBidByLotIdAndMaxSize(lot.getId());
        if (bid != null) {
            lot.setStatus(LotStatus.SOLD);
            //bidRepository.deleteAllBetsInLots(lot.getId());
            // Уведомление покупателю
            notificationService.createNotification(
                    NotificationType.LOT_PURCHASED,
                    "TitleLotPurchased",
                    "BodyLotPurchased",
                    bid.getOwner());
            // Уведомление продавцу
            notificationService.createNotification(
                    NotificationType.LOT_SOLD,
                    "TitleLotSold",
                    "BodyLotSold",
                    lot.getOwner());
        } else {
            //Уведомление продавцу
            lot.setStatus(LotStatus.UNSOLD);
            lotRepository.save(lot);
            notificationService.createNotification(
                    NotificationType.LOT_EXPIRED,
                    "TitleLotExpired",
                    "BodyLotExpired",
                    lot.getOwner());
            log.info(String.format("Lot %s not sold", lot.getTitle()));
        }
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
                Bid bid = bidRepository.getBidByLotAndStatusActual(lot);
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
