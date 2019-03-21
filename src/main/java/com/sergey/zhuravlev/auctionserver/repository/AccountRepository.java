package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByUsername(String username);

    Optional<Account> findAccountByUser(User user);

}
