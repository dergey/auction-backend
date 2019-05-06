package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.database.entity.Bid;
import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDto;

public class BidConverter {

    public static ResponseBidDto convert(Bid bid) {
        if (bid == null) return null;
        ResponseBidDto bidDto = new ResponseBidDto();
        bidDto.setId(bid.getId());
        bidDto.setCreateAt(bid.getCreateAt());
        bidDto.setStatus(bid.getStatus());
        bidDto.setAmount(bid.getAmount());
        bidDto.setCurrencyCode(bid.getCurrency().getCurrencyCode());
        bidDto.setLotId(bid.getLot().getId());
        return bidDto;
    }

}
