package com.sergey.zhuravlev.auctionserver.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationCodeDto {

    @JsonProperty("authorization_code")
    private String authorizationCode;

}
