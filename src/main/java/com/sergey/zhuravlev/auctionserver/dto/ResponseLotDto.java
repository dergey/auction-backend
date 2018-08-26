package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseLotDto extends LotDto {

    @JsonProperty(value = "id", index = 0)
    private Long id;
    @JsonProperty(value = "owner")
    private User owner;
    @JsonProperty(value = "status")
    private Status status;
    @JsonProperty(value = "last_bid")
    private Bid lastBid;

}
