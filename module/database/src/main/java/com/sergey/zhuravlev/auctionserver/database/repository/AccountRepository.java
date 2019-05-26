package com.sergey.zhuravlev.auctionserver.database.repository;

import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findAccountByUser(User user);

    Optional<Account> findAccountByUsername(String username);

    Optional<Account> findAccountByUserPrincipalEmail(String email);

    Boolean existsByUsername(String username);

}
