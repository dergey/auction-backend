package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.core.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.entity.ForeignUser;
import com.sergey.zhuravlev.auctionserver.database.entity.LocalUser;
import com.sergey.zhuravlev.auctionserver.database.entity.Principal;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository
                .findByPrincipalEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("User with email %s not found", email)));
    }

    @Transactional
    public LocalUser createLocalUser(String email, String rawPassword) {
        if (userRepository.existsByPrincipalEmail(email)) {
            throw new BadRequestException("email has created");
        }
        Principal principal = new Principal(null, email, null);
        LocalUser localUser = new LocalUser();
        localUser.setPrincipal(principal);
        localUser.setPassword(passwordEncoder.encode(rawPassword));
        localUser = userRepository.save(localUser);
        return localUser;
    }

    @Transactional
    public ForeignUser createForeignUser(String email, AuthProvider provider, String providerId) {
        if (userRepository.existsByPrincipalEmail(email)) {
            throw new BadRequestException("Email already created");
        }
        Principal principal = new Principal(null, email, null);
        ForeignUser foreignUser = new ForeignUser();
        foreignUser.setPrincipal(principal);
        foreignUser.setProvider(provider);
        foreignUser.setProviderId(providerId);
        foreignUser = userRepository.save(foreignUser);
        return foreignUser;
    }

    @Transactional
    public LocalUser updatePassword(LocalUser user, String oldPassword, String newPassword) {
        user = (LocalUser) userRepository
                .findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Update exception: username has created");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

}
