package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dto.RequestBidDto;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import com.sergey.zhuravlev.auctionserver.enums.LotStatus;
import com.sergey.zhuravlev.auctionserver.repository.BidRepository;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final LotService lotService;
    private final BidRepository bidRepository;

    @Transactional(readOnly = true)
    public Bid getBid(Long id) {
        return bidRepository.getBidById(id);
    }

    @Transactional
    public Bid createBid(User user, RequestBidDto requestBidDto) {
        Date now = new Date();
        Lot lot = lotService.getLot(requestBidDto.getLotId());
        if (!lot.getStatus().equals(LotStatus.ACTIVE)) throw new RuntimeException("Lot is not active");
        if (!lot.getCurrency().getCurrencyCode().equals(requestBidDto.getCurrencyCode())) throw new RuntimeException("Currency conversion post MVP");
        Bid currentBid = bidRepository.getBidByLotAndStatusActual(lot);
        if (currentBid != null) {
            if (requestBidDto.getAmount().compareTo(currentBid.getAmount()) <= 0) throw new RuntimeException("Actual bid have greet amount");
            currentBid.setStatus(BidStatus.BROKEN);
        }
        Bid bid = new Bid(
                null,
                now,
                requestBidDto.getAmount(),
                Currency.getInstance(requestBidDto.getCurrencyCode()),
                BidStatus.ACTUAL,
                lot,
                user);
        bid = bidRepository.save(bid);
        return bid;
        //TODO Уведомление предыдущему владельцу лоту
        //log.debug("Ставка  : " + currentBid);
        //TODO move to aspect
        //if (currentBid != null && !currentBid.getBuyer().getNotificationToken().isEmpty()) {
        //  log.debug("SEND MESSAGE : buyer - " + currentBid.getBuyer().getUsername() + " new bid size - " + bid.getSize() + " lot " + lot);
        //  notificationService.send(new NotificationBetBroken(currentBid.getBuyer(), bid, lot));
        //}
    }

    @Transactional
    public Bid cancelBid(Bid bid) {
        bid = bidRepository.getBidById(bid.getId());
        if (bid.getStatus().equals(BidStatus.CANCELED) || bid.getStatus().equals(BidStatus.SUCCESSFUL)) {
            throw new RuntimeException("Already in finished status");
        }
        if (bid.getStatus().equals(BidStatus.ACTUAL)) {
            Bid oldBid = bidRepository.getBidByLotIdAndMaxSize(bid.getLot().getId());
            oldBid.setStatus(BidStatus.ACTUAL);
        }
        bid.setStatus(BidStatus.CANCELED);
        return bid;
    }

}
