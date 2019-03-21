package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dto.ResponseAccountDto;
import com.sergey.zhuravlev.auctionserver.dto.AccountRequestDto;
import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.service.AccountService;
import com.sergey.zhuravlev.auctionserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final ImageService imageService;

    @GetMapping(value = "{username}")
    public ResponseAccountDto getAccount(@PathVariable("username") String username) {
        Account account = accountService.getAccountByUsername(username);
        return accountService.getAccountResponseDto(account);
    }

    @PostMapping
    public ResponseAccountDto createAccount(AccountRequestDto accountRequestDto) {

        User user = null;
        Image photo = imageService.getImage(accountRequestDto.getPhotoId());
        Account account = accountService.createAccount(
                user,
                accountRequestDto.getUsername(),
                photo,
                accountRequestDto.getFirstname(),
                accountRequestDto.getLastname(),
                accountRequestDto.getBio());
        return accountService.getAccountResponseDto(account);
    }

}
