package com.sergey.zhuravlev.auctionserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestUserDto extends UserDto {

    private String password;
    private String email;

}
