package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.BidConverter;
import com.sergey.zhuravlev.auctionserver.core.service.BidService;
import com.sergey.zhuravlev.auctionserver.core.service.LotService;
import com.sergey.zhuravlev.auctionserver.database.builder.BidPredicateBuilder;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Bid;
import com.sergey.zhuravlev.auctionserver.database.entity.Lot;
import com.sergey.zhuravlev.auctionserver.dto.PageDto;
import com.sergey.zhuravlev.auctionserver.dto.RequestBidDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDto;
import com.sergey.zhuravlev.auctionserver.faucet.SecurityFaucet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Currency;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;
    private final SecurityFaucet securityFaucet;
    private final LotService lotService;

    @GetMapping
    public PageDto<ResponseBidDto> list(@RequestParam(value = "size", required = false) Integer size,
                                        @RequestParam(value = "page", required = false) Integer page) {
        return new PageDto<>(bidService.list(new BidPredicateBuilder(), PageRequest.of(page == null ? 0 : page, size == null ? PageDto.DEFAULT_PAGE_SIZE : size))
                .map(BidConverter::convert));
    }

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseBidDto createBid(@Valid @RequestBody RequestBidDto bidDto) {
        Account account = securityFaucet.getCurrentAccount();
        Lot lot = lotService.getLot(bidDto.getLotId());
        Bid bid = bidService.createBid(account, lot, bidDto.getAmount(), Currency.getInstance(bidDto.getCurrencyCode()));
        return BidConverter.convert(bid);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "{bid_id}")
    public ResponseBidDto cancelBid(@PathVariable("bid_id") Long bidId) {
        Bid bid = bidService.getBid(bidId);
        bid = bidService.cancelBid(bid);
        return BidConverter.convert(bid);
    }

}
