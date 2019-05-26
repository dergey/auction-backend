package com.sergey.zhuravlev.auctionserver.socket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class StompPrincipal implements Principal {
    private final String name;
}
