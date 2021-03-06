package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.core.service.AccountService;
import com.sergey.zhuravlev.auctionserver.core.service.ImageService;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.dto.UserDto;
import com.sergey.zhuravlev.auctionserver.dto.auth.AuthResponseDto;
import com.sergey.zhuravlev.auctionserver.dto.auth.LoginRequestDto;
import com.sergey.zhuravlev.auctionserver.dto.auth.SingUpRequestDto;
import com.sergey.zhuravlev.auctionserver.faucet.UserFaucet;
import com.sergey.zhuravlev.auctionserver.security.service.TokenProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final UserFaucet userFaucet;

    private final ImageService imageService;
    private final AccountService accountService;

    private final AuthenticationManager authenticationManager;
    private final TokenProviderService tokenProviderService;


    @PostMapping("/authenticate")
    public AuthResponseDto authenticate(@Valid @RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProviderService.createToken(authentication);
        return new AuthResponseDto(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registration(@Valid @RequestBody SingUpRequestDto singUpRequestDto) {
        Account account = accountService.createLocalAccount(
                singUpRequestDto.getEmail(),
                singUpRequestDto.getPassword(),
                singUpRequestDto.getUsername(),
                singUpRequestDto.getPhoto() != null ? imageService.getImage(singUpRequestDto.getPhoto()) : null,
                singUpRequestDto.getFirstname(),
                singUpRequestDto.getLastname(),
                singUpRequestDto.getBio());
        return userFaucet.getUserDto(account.getUser());
    }

}
