package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
abstract class LotDto {

    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "description")
    private String description;
    @JsonProperty(value = "image")
    private String image;
    @JsonProperty(value = "expiration_date")
    private Date expirationDate;
    @JsonProperty(value = "starting_price")
    private Double startingPrice;
    @JsonProperty(value = "auction_step")
    private Double auctionStep;
    @JsonProperty(value = "category")
    private Category category;

}
