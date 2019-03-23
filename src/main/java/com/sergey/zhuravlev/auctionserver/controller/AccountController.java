package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.AccountConverter;
import com.sergey.zhuravlev.auctionserver.dto.AccountRequestDto;
import com.sergey.zhuravlev.auctionserver.dto.AccountResponseDto;
import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.faucet.SecurityFaucet;
import com.sergey.zhuravlev.auctionserver.service.AccountService;
import com.sergey.zhuravlev.auctionserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final SecurityFaucet securityFaucet;
    private final AccountService accountService;
    private final ImageService imageService;

    @GetMapping(value = "{username}")
    public AccountResponseDto getAccount(@PathVariable("username") String username) {
        Account account = accountService.getAccountByUsername(username);
        return AccountConverter.getAccountResponseDto(account);
    }

    @PostMapping
    public AccountResponseDto createAccount(@Valid @RequestBody AccountRequestDto accountRequestDto) {
        User user = securityFaucet.getCurrentUser();
        Image photo = imageService.getImage(accountRequestDto.getPhoto());
        Account account = accountService.createAccount(
                user,
                accountRequestDto.getUsername(),
                photo,
                accountRequestDto.getFirstname(),
                accountRequestDto.getLastname(),
                accountRequestDto.getBio());
        return AccountConverter.getAccountResponseDto(account);
    }

}
