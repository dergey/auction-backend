package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.LocalUser;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserService userService;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountByUsername(String username) {
        return accountRepository
                .findAccountByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional
    public Account createLocalAccount(String email, String password, String username, Image photo,
                                 String firstname, String lastname, String bio) {
        LocalUser localUser = userService.createLocalUser(email, password);
        return createAccount(localUser, username, photo, firstname, lastname, bio);
    }

    @Transactional
    public Account createAccount(User user, String username, Image photo, String firstname, String lastname, String bio) {
        Account account = new Account();
        account.setUser(user);
        account.setUsername(username);
        account.setPhoto(photo);
        account.setFirstname(firstname);
        account.setLastname(lastname);
        account.setBio(bio);
        account = accountRepository.save(account);
        return account;
    }
}
