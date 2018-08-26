package com.sergey.zhuravlev.auctionserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
abstract class BidDto {

    private Double size;

}
