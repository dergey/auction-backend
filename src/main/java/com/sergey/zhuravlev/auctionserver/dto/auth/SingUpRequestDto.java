package com.sergey.zhuravlev.auctionserver.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergey.zhuravlev.auctionserver.dto.AccountRequestDto;
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
public class SingUpRequestDto extends AccountRequestDto {

    @NotBlank
    @Email
    @JsonProperty(value = "email")
    private String email;

    @NotBlank
    @JsonProperty(value = "password")
    private String password;

}
