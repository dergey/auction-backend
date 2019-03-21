package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAccountDto {

    @JsonProperty(value = "email")
    private String username;

    @JsonProperty(value = "photo")
    private String photo;

    @JsonProperty(value = "firstname")
    private String firstname;

    @JsonProperty(value = "lastname")
    private String lastname;

    @JsonProperty(value = "stars")
    private Double stars;

    @JsonProperty(value = "bio")
    private String bio;

}
