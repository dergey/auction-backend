package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.BidConverter;
import com.sergey.zhuravlev.auctionserver.dto.RequestBidDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDto;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    //TODO added
//    @GetMapping
//    public List<ResponseBidDto> list() {
//        return bidService.getAllBetByLotId(lotId);
//    }

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseBidDto createBid(@Validated @RequestBody RequestBidDto bidDto,
                                    Principal principal) {
        Bid bid = bidService.createBid((User) principal, bidDto);
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
