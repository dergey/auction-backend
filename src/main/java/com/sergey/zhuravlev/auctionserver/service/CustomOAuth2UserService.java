package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.ForeignUser;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.enums.UserType;
import com.sergey.zhuravlev.auctionserver.exception.OAuth2AuthenticationProcessingException;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.security.UserPrincipal;
import com.sergey.zhuravlev.auctionserver.security.user.OAuth2UserInfo;
import com.sergey.zhuravlev.auctionserver.security.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private static AuthProvider getAuthProvider(String registrationId) {
        switch (registrationId) {
            case "google":
                return AuthProvider.GOOGLE;
            case "github":
                return AuthProvider.GITHUB;
            case "facebook":
                return AuthProvider.FACEBOOK;
            default:
                throw new OAuth2AuthenticationProcessingException(
                        String.format("Not supported provider service %s", registrationId));
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByPrincipalEmail(oAuth2UserInfo.getEmail());
        AuthProvider provider = getAuthProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getUserType() == UserType.LOCAL) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with "
                        + provider
                        + " account. Please use your local account to login.");
            } else if (user.getUserType() == UserType.FOREIGN) {
                if (!((ForeignUser) user).getProvider().equals(provider)) {
                    throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with "
                            + provider
                            + " account. Please use your " + ((ForeignUser) user).getProvider()
                            + " account to login.");
                }
            } else {
                //TODO
                throw new OAuth2AuthenticationProcessingException("Ops");
            }
        } else {
            user = registerNewForeignUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create((ForeignUser) user, oAuth2User.getAttributes());
    }

    private ForeignUser registerNewForeignUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
       return userService.createForeignUser(
                oAuth2UserInfo.getEmail(),
                getAuthProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId()),
                oAuth2UserInfo.getId()
        );
    }

}
