package com.sergey.zhuravlev.auctionserver.service;

import com.querydsl.core.types.Predicate;
import com.sergey.zhuravlev.auctionserver.builder.LotPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.dto.RequestLotDto;
import com.sergey.zhuravlev.auctionserver.entity.*;
import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.repository.CategoryRepository;
import com.sergey.zhuravlev.auctionserver.repository.ImageRepository;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Collection<Lot> list(LotStatus lotStatus, String titleLike, String owner, String category, Integer pageNumber, Integer pageSize) {
        Predicate lotPredicate = new LotPredicateBuilder()
                .withStatus(lotStatus)
                .withTitleLike(titleLike)
                .withOwnerName(owner)
                .withCategoryName(category)
                .build();

        Page<Lot> page = lotRepository.findAll(lotPredicate, PageRequest.of(pageNumber, pageSize,
                Sort.Direction.ASC, "firstName"));
        return page.getContent();
    }

    @Transactional
    public Lot createLot(RequestLotDto lotDto, Account account) {
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
                null,
                LotStatus.ACTIVE,
                account,
                categoryOptional.get());
        lotRepository.save(lot);
        return lot;
    }

    @Transactional
    public Lot updateLot(Lot lot, Account account, RequestLotDto lotDto) {
        Date now = new Date();
        lot = lotRepository.findByIdAndOwner(lot.getId(), account).orElseThrow(() -> new NotFoundException("Lot not found"));
        //Обновлятся могут только отмененые и не проданые!
        if (lotDto.getExpiresAt().equals(lot.getExpiresAt()) ||
                lotDto.getExpiresAt().after(lot.getExpiresAt())) throw new BadRequestException("Not possible update expires date");
        if (lotDto.getExpiresAt().before(now)) throw new BadRequestException("Expires date will be after create date");
        Category category = categoryRepository
                .findById(lotDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", lotDto.getCategoryId())));
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
