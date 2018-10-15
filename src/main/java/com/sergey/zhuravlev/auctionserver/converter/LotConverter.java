package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.ResponseLotDto;
import com.sergey.zhuravlev.auctionserver.entity.Lot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LotConverter {

    public static ResponseLotDto toResponse(Lot lot) {
        ResponseLotDto lotDto = new ResponseLotDto();
        lotDto.setId(lot.getId());
        lotDto.setTitle(lot.getTitle());
        lotDto.setStartingPrice(lot.getStartingPrice());
        lotDto.setImage(lot.getImage());
        lotDto.setExpirationDate(lot.getExpirationDate());
        lotDto.setDescription(lot.getDescription());
        lotDto.setCategory(lot.getCategory());
        lotDto.setAuctionStep(lot.getAuctionStep());
        lotDto.setStatus(lot.getStatus());
        lotDto.setOwner(lot.getOwner());
        return lotDto;
    }

    public static List<ResponseLotDto> toResponseCollection(Iterable<Lot> lots) {
        List <ResponseLotDto> result = new ArrayList<>();
        for (Lot lot : lots) {
            result.add(toResponse(lot));
        }
        return result;
    }

}
