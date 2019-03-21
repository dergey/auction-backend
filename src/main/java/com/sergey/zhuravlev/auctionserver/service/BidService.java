package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dto.RequestBidDto;
import com.sergey.zhuravlev.auctionserver.entity.*;
import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Bid getBid(Long id) {
        return bidRepository.getBidById(id);
    }

    @Transactional
    public Bid createBid(Account account, RequestBidDto requestBidDto) {
        Date now = new Date();
        Lot lot = lotRepository.findById(requestBidDto.getLotId()).orElseThrow(() -> new NotFoundException("Lot not found"));
        if (!lot.getStatus().equals(LotStatus.ACTIVE)) throw new RuntimeException("Lot is not active");
        if (!lot.getCurrency().getCurrencyCode().equals(requestBidDto.getCurrency())) throw new BadRequestException("Currency conversion post MVP");
        Bid currentBid = lot.getCurrentBid();
        if (currentBid != null) {
            if (requestBidDto.getAmount().compareTo(currentBid.getAmount()) <= 0) throw new BadRequestException("Actual bid have greet amount");
            currentBid.setStatus(BidStatus.BROKEN);
        }

        Bid bid = new Bid(
                null,
                now,
                requestBidDto.getAmount(),
                Currency.getInstance(requestBidDto.getCurrency()),
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
