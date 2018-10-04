package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.BidConverter;
import com.sergey.zhuravlev.auctionserver.dto.RequestBidDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDto;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.service.BidService;
import com.sergey.zhuravlev.auctionserver.service.LotService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Log
@RestController
public class BidController {

    private final LotService lotService;
    private final BidService bidService;

    @Autowired
    public BidController(LotService lotService, BidService bidService) {
        this.lotService = lotService;
        this.bidService = bidService;
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/lots/bid")
    public ResponseBidDto createBid(@Validated @RequestBody RequestBidDto bidDto, Principal principal) {
        //FIXME test it work ?
        User user = (User) principal;
        Lot lot = lotService.get(bidDto.getLotId());
        Bid bid = bidService.create(lot, bidDto.getSize(), user);
        return BidConverter.toResponse(bid);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/profile/bids/{id}")
    public void deleteBid(@PathVariable("id") Long id) {
        bidService.delete(id);
    }

    @GetMapping(value = "/lot/{id}/bids/")
    public List<Bid> getAllBidsForLot(@PathVariable("id") Long lotId) {
        return bidService.getAllBetByLotId(lotId);
    }

    @GetMapping(value = "/lot/{id}/bids/last/")
    public Bid getLastBid(@PathVariable("id") Long lotId) {
        return bidService.getLastBid(lotId);
    }

}
