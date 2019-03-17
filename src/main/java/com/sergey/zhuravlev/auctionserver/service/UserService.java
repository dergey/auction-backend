package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User get(String username) {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new NotFoundException(String.format("User with username %s not found", username)));
    }

    @Transactional
    public User create(String email, String username, Image photo, String rawPassword,
                       AuthProvider provider, String providerId, Boolean emailVerified,
                       String firstname, String lastname, String bio) {

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("email has created");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("username has created");
        }

        User user = new User(null,
                email,
                username,
                photo,
                passwordEncoder.encode(rawPassword),
                provider,
                providerId,
                emailVerified,
                firstname,
                lastname,
                null,
                bio);
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(Long id, String username, Image photo, String rawPassword,
                       AuthProvider provider, String providerId, Boolean emailVerified,
                       String firstname, String lastname, String bio) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("username has created");
        }

        user.setUsername(username);
        user.setPhoto(photo);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmailVerified(emailVerified);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setBio(bio);
        return user;
    }

}
