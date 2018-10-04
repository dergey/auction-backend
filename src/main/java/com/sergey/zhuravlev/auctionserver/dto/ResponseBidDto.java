package com.sergey.zhuravlev.auctionserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
public class ResponseBidDto extends BidDto {

    private Long id;
    private ResponseLotDto lot;

}
