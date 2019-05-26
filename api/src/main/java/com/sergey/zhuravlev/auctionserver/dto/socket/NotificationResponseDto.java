package com.sergey.zhuravlev.auctionserver.dto.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.database.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    @JsonProperty(value = "type")
    private NotificationType type;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "body")
    private String body;

    @JsonProperty(value = "create_at")
    private Date createAt;

    @JsonProperty(value = "send")
    private Boolean send;

}
