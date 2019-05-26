package com.sergey.zhuravlev.auctionserver.dto.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    @JsonProperty(value = "last_connection")
    private Date lastConnection;

}
