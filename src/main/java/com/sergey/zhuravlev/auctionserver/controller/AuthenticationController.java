package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.UserConverter;
import com.sergey.zhuravlev.auctionserver.dto.auth.AuthResponseDto;
import com.sergey.zhuravlev.auctionserver.dto.auth.LoginRequestDto;
import com.sergey.zhuravlev.auctionserver.dto.RequestUserDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseUserDto;
import com.sergey.zhuravlev.auctionserver.dto.auth.SingUpRequestDto;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.service.ImageService;
import com.sergey.zhuravlev.auctionserver.service.TokenProviderService;
import com.sergey.zhuravlev.auctionserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticationController {

    private final UserService userService;
    private final ImageService imageService;
    private final AuthenticationManager authenticationManager;

    private final TokenProviderService tokenProviderService;

    @PostMapping("/login")
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

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto registration(@Validated @RequestBody SingUpRequestDto userDto) {
        Image image = imageService.getImage(userDto.getPhotoId());
        User user = userService.create(userDto.getEmail(), userDto.getUsername(), image,
                userDto.getPassword(), AuthProvider.LOCAL, null, false,
                userDto.getFirstname(), userDto.getLastname(), userDto.getBio());
        return UserConverter.convert(user);
    }

}
