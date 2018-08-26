package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class UserDto {

    @JsonProperty(value = "username")
    private String username;
    @JsonProperty(value = "firstname")
    private String firstname;
    @JsonProperty(value = "lastname")
    private String lastname;
    @JsonProperty(value = "history")
    private String history;

}
