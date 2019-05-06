package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {

    @NotBlank
    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "photo")
    private String photo;

    @NotBlank
    @JsonProperty(value = "firstname")
    private String firstname;

    @NotBlank
    @JsonProperty(value = "lastname")
    private String lastname;

    @JsonProperty(value = "bio")
    private String bio;

}
