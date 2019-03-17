package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.exception.OAuth2AuthenticationProcessingException;
import com.sergey.zhuravlev.auctionserver.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.security.UserPrincipal;
import com.sergey.zhuravlev.auctionserver.security.user.OAuth2UserInfo;
import com.sergey.zhuravlev.auctionserver.security.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ImageService imageService;

    private final RestTemplate restTemplate;

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

        User user;
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(getAuthProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        user = userRepository.save(user);
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        ResponseEntity<byte[]> imageResponse = restTemplate.getForEntity(oAuth2UserInfo.getImageUrl(), byte[].class);

        Image userPhoto = null;

        if (imageResponse.getStatusCode().is2xxSuccessful() && imageResponse.hasBody()) {
            userPhoto = imageService.save(imageResponse.getBody());
        }

        //TODO ADDED NewUser entity
        return userService.create(
                oAuth2UserInfo.getEmail(),
                StringUtils.trimAllWhitespace(oAuth2UserInfo.getName().toLowerCase()),
                userPhoto,
                RandomStringUtils.randomAlphanumeric(16),
                getAuthProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId()),
                oAuth2UserInfo.getId(),
                false,
                "",
                "",
                ""
        );
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        byte[] imageBytes = restTemplate.getForObject(oAuth2UserInfo.getImageUrl(), byte[].class);
        Image userPhoto = imageService.save(imageBytes);
        return userService.update(
                existingUser.getId(),
                existingUser.getUsername(),
                userPhoto,
                existingUser.getPassword(),
                existingUser.getProvider(),
                existingUser.getProviderId(),
                existingUser.getEmailVerified(),
                existingUser.getFirstname(),
                existingUser.getLastname(),
                existingUser.getBio()
        );
    }

}
