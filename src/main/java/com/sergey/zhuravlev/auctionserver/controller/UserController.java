package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.UserConverter;
import com.sergey.zhuravlev.auctionserver.dto.RequestUserDto;
import com.sergey.zhuravlev.auctionserver.dto.ResponseUserDto;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.service.SecurityService;
import com.sergey.zhuravlev.auctionserver.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/profile")
    public ResponseUserDto getUserProfile() {
        User user = SecurityService.getAuthenticationUser();
        return UserConverter.toResponse(user);
    }

    @GetMapping(value = "/users/{username}")
    public ResponseUserDto getAnotherProfile(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return UserConverter.toResponse(user);
    }

    @PostMapping(value = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto registration(@Validated @RequestBody RequestUserDto userDto) {
        User user = userService.create(userDto.getUsername(), userDto.getPassword(),
                userDto.getFirstname(), userDto.getLastname(),
                userDto.getEmail(), userDto.getHistory());
        securityService.autoLogin(user.getUsername(), user.getPassword());
        return UserConverter.toResponse(user);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/profile/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registrationNotificationKey(@RequestBody String token) {
        if (!token.isEmpty()) {
            User user = SecurityService.getAuthenticationUser();
            userService.updateNotificationKey(user, token);
        }
    }

    @Secured({"ROLE_USER"})
    @DeleteMapping(value = "/profile/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterNotificationKey() {
        User user = SecurityService.getAuthenticationUser();
        userService.deleteNotificationToken(user.getId());
    }

}
