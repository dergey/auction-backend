package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDto;
import com.sergey.zhuravlev.auctionserver.entity.Bid;

public class BidConverter {

    public static ResponseBidDto toResponse(Bid bid) {
        ResponseBidDto bidDto = new ResponseBidDto();
        bidDto.setId(bid.getId());
        bidDto.setLot(LotConverter.toResponse(bid.getLot()));
        bidDto.setSize(bid.getSize());
        return bidDto;
    }

}
