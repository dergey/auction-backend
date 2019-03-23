package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.converter.AccountConverter;
import com.sergey.zhuravlev.auctionserver.dto.UserDto;
import com.sergey.zhuravlev.auctionserver.entity.*;
import com.sergey.zhuravlev.auctionserver.enums.AuthProvider;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.AccountRepository;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    public UserDto getUserDto(User user) {
        user = userRepository
                .findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getPrincipal().getEmail());
        userDto.setPhone(user.getPrincipal().getPhone());
        Account account = accountRepository.findAccountByUser(user).orElse(null);
        userDto.setAccount(AccountConverter.getAccountResponseDto(account));
        return userDto;
    }
}
