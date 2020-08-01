package com.sergey.zhuravlev.auctionserver.security.service;

import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class OAuth2AuthorizationCodeRequest extends AbstractOAuth2AuthorizationGrantRequest {

    private final ClientRegistration clientRegistration;
    private final String authorizationCode;

    public OAuth2AuthorizationCodeRequest(ClientRegistration clientRegistration, String authorizationCode) {
        super(AuthorizationGrantType.AUTHORIZATION_CODE);
        this.clientRegistration = clientRegistration;
        this.authorizationCode = authorizationCode;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }
}
