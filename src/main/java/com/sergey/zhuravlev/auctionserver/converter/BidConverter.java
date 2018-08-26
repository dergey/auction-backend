package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.ResponseBidDot;
import com.sergey.zhuravlev.auctionserver.entity.Bid;

public class BidConverter {

    public static ResponseBidDot toResponse(Bid bid) {
        ResponseBidDot bidDto = new ResponseBidDot();
        bidDto.setId(bid.getId());
        bidDto.setLot(LotConverter.toResponse(bid.getLot()));
        bidDto.setSize(bid.getSize());
        return bidDto;
    }

}
