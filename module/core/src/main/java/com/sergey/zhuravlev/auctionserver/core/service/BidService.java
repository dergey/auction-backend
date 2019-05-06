package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.core.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.builder.BidPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Bid;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.database.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.database.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.database.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.database.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class BidService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<Bid> list(BidPredicateBuilder builder, Pageable pageable) {
        return bidRepository.findAll(builder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Bid getBid(Long id) {
        return bidRepository.getBidById(id);
    }

    @Transactional
    public Bid createBid(Account account, Lot lot, Long amount, Currency currency) {
        Date now = new Date();
        lot = lotRepository.findById(lot.getId()).orElseThrow(() -> new NotFoundException("Lot not found"));
        if (!lot.getStatus().equals(LotStatus.ACTIVE)) throw new RuntimeException("Lot is not active");
        if (!lot.getCurrency().equals(currency)) throw new BadRequestException("Currency conversion not support!");
        Bid currentBid = lot.getCurrentBid();
        if (currentBid != null) {
            if (amount.compareTo(currentBid.getAmount()) <= 0) throw new BadRequestException("Actual bid have greet amount");
            currentBid.setStatus(BidStatus.BROKEN);
        }

        Bid bid = new Bid(
                null,
                now,
                amount,
                currency,
                BidStatus.CURRENT,
                lot,
                account);

        bid = bidRepository.save(bid);
        lot.setCurrentBid(bid);

        notificationService.createNotificationBidBroken(currentBid);
        notificationService.createNotificationNewBid(bid);

        return bid;
    }

    @Transactional
    public Bid cancelBid(Bid bid) {
        bid = bidRepository.getBidById(bid.getId());
        if (bid.getStatus().equals(BidStatus.CANCELED) || bid.getStatus().equals(BidStatus.SUCCESSFUL)) {
            throw new BadRequestException("Bid already in finished status");
        }
        if (bid.getStatus().equals(BidStatus.CURRENT)) {
            //TODO change logic or add notification ???
            Bid oldBid = bidRepository.getBidByLotIdAndMaxSize(bid.getLot().getId());
            oldBid.setStatus(BidStatus.CURRENT);
        }
        bid.setStatus(BidStatus.CANCELED);
        return bid;
    }

}
