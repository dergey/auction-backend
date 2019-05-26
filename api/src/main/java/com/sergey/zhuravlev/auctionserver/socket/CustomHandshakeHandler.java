package com.sergey.zhuravlev.auctionserver.socket;

import com.sergey.zhuravlev.auctionserver.core.service.AccountService;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.security.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final AccountService accountService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Account account = accountService.getAccountByUserEmail(userPrincipal.getEmail());
        return new StompPrincipal(account.getUsername());
    }

}
