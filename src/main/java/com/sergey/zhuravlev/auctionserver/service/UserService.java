package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.model.User;

public interface UserService {

    void save(User user);

    void update(User user);

    User findByUsername(String username);
    User findByUsernameWithoutPassword(String username);
    User getUserWithoutPassword(Long id);

    User getOne(Long userID);

    void deleteNotificationToken(Long id);
}
