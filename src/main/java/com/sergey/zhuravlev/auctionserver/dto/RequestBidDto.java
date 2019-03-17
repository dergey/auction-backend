package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestBidDto {

    @JsonProperty(value = "amount")
    private Long amount;

    @JsonProperty(value = "currency_code")
    private String currencyCode;

    @JsonProperty(value = "lot_id")
    private Long lotId;

}
