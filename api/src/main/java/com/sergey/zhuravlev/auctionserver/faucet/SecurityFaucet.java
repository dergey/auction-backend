package com.sergey.zhuravlev.auctionserver.faucet;

import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.repository.AccountRepository;
import com.sergey.zhuravlev.auctionserver.database.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.security.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityFaucet {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Optional<User> user = userRepository.findByPrincipalEmail(principal.getEmail());
        return user.orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByPrincipalEmail(principal.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        return accountRepository.findAccountByUser(user).orElseThrow(() -> new NotFoundException("Account not found"));
    }


}
