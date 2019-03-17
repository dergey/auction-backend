package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLotDto {

    @NotEmpty
    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "images")
    private Collection<String> images;

    @Future
    @JsonProperty(value = "expires_at")
    private Date expiresAt;

    @Positive
    @JsonProperty(value = "starting_amount")
    private BigDecimal startingAmount;

    @Size(max = 3, min = 3)
    @JsonProperty(value = "currency_code")
    private String currencyCode;

    @Positive
    @JsonProperty(value = "auction_step")
    private Long auctionStep;

    @NotNull
    @JsonProperty(value = "category_id")
    private Long categoryId;

}
