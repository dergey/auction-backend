package com.sergey.zhuravlev.auctionserver.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingUpRequestDto {

    @NotBlank
    @Email
    @JsonProperty(value = "email")
    private String email;

    @NotBlank
    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "photo")
    private String photoId;

    @NotBlank
    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "firstname")
    private String firstname;

    @JsonProperty(value = "lastname")
    private String lastname;

    @JsonProperty(value = "bio")
    private String bio;

}
