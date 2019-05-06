package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.database.enums.LotStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLotDto {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "images")
    private Collection<String> images;

    @JsonProperty(value = "create_at")
    private Date createAt;

    @JsonProperty(value = "update_at")
    private Date updateAt;

    @JsonProperty(value = "expires_at")
    private Date expiresAt;

    @JsonProperty(value = "starting_amount")
    private Long startingAmount;

    @JsonProperty(value = "currency")
    private String currencyCode;

    @JsonProperty(value = "auction_step")
    private Long auctionStep;

    @JsonProperty(value = "status")
    private LotStatus status;

    @JsonProperty(value = "owner")
    private String owner;

    @JsonProperty(value = "category")
    private String category;

}
