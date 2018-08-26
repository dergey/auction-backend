package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.RequestUserDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseUserDto;
import com.sergey.zhuravlev.auctionserver.entity.User;

public class UserConverter {

    public static ResponseUserDto toResponse(User user) {
        ResponseUserDto userDto = new ResponseUserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setHistory(user.getHistory());
        userDto.setRating(user.getRating());
        return userDto;
    }

    public static User fromRequest(RequestUserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setHistory(userDto.getHistory());
        return user;
    }

}
