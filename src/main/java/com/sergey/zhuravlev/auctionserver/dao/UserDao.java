package com.sergey.zhuravlev.auctionserver.dao;

import com.sergey.zhuravlev.auctionserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Modifying
    @Query(value = "UPDATE `user` SET notification_token = NULL", nativeQuery = true)
    @Transactional
    void deleteAllNotificationTokens();

    @Modifying
    @Query(value = "UPDATE `user` SET notification_token = NULL WHERE id = ?1", nativeQuery = true)
    @Transactional
    void deleteNotificationToken(Long id);

}
