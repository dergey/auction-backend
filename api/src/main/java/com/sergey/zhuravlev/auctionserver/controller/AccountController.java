package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.AccountConverter;
import com.sergey.zhuravlev.auctionserver.core.service.AccountService;
import com.sergey.zhuravlev.auctionserver.core.service.ImageService;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.dto.AccountRequestDto;
import com.sergey.zhuravlev.auctionserver.dto.AccountResponseDto;
import com.sergey.zhuravlev.auctionserver.faucet.SecurityFaucet;
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
        Account account = accountService.createUpdateAccount(
                user,
                accountRequestDto.getUsername(),
                accountRequestDto.getPhoto() != null && !accountRequestDto.getPhoto().isEmpty() ?
                        imageService.getImage(accountRequestDto.getPhoto()) :
                        null,
                accountRequestDto.getFirstname(),
                accountRequestDto.getLastname(),
                accountRequestDto.getBio());
        return AccountConverter.getAccountResponseDto(account);
    }

}
