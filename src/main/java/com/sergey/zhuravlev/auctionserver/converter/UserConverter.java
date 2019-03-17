package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.ResponseUserDto;
import com.sergey.zhuravlev.auctionserver.entity.User;

public class UserConverter {

    public static ResponseUserDto convert(User user) {
        if (user == null) return null;
        ResponseUserDto userDto = new ResponseUserDto();
        userDto.setUsername(user.getUsername());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setBio(user.getBio());
        return userDto;
    }

}
