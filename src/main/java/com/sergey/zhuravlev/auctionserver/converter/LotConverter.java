package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.ResponseLotDto;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.Lot;

import java.util.stream.Collectors;

public class LotConverter {

    public static ResponseLotDto convert(Lot lot) {
        if (lot == null) return null;
        ResponseLotDto lotDto = new ResponseLotDto();
        lotDto.setId(lot.getId());
        lotDto.setTitle(lot.getTitle());
        lotDto.setDescription(lot.getDescription());
        lotDto.setImages(lot.getImages().stream().map(Image::getName).collect(Collectors.toList()));
        lotDto.setCreateAt(lot.getCreateAt());
        lotDto.setUpdateAt(lot.getUpdateAt());
        lotDto.setExpiresAt(lot.getExpiresAt());
        lotDto.setStartingAmount(lot.getStartingAmount());
        lotDto.setCurrencyCode(lot.getCurrency().getCurrencyCode());
        lotDto.setAuctionStep(lot.getAuctionStep());
        lotDto.setStatus(lot.getStatus());
        lotDto.setOwner(lot.getOwner().getUsername());
        lotDto.setCategory(lot.getCategory().getName());
        return lotDto;
    }

}
