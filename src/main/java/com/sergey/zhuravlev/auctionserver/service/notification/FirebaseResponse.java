package com.sergey.zhuravlev.auctionserver.service.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class FirebaseResponse {

    private long multicast_id;
    private Integer success;
    private Integer failure;
    private Object canonical_ids;
    private Object results;

}


