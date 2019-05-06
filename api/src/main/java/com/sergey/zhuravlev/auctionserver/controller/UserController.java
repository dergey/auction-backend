package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.dto.UserDto;
import com.sergey.zhuravlev.auctionserver.faucet.SecurityFaucet;
import com.sergey.zhuravlev.auctionserver.faucet.UserFaucet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final SecurityFaucet securityFaucet;
    private final UserFaucet userFaucet;

    @GetMapping
    public UserDto home() {
        User user = securityFaucet.getCurrentUser();
        return userFaucet.getUserDto(user);
    }

}
