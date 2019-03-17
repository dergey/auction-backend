package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBidDto {

    @JsonProperty(value = "id", index = 0)
    private Long id;

    @JsonProperty(value = "create_at")
    private Date createAt;

    @JsonProperty(value = "status")
    private BidStatus status;

    @JsonProperty(value = "amount")
    private Long amount;

    @JsonProperty(value = "currency_code")
    private String currencyCode;

    @JsonProperty(value = "lot_id")
    private Long lotId;

}
