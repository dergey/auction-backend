package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.dao.RoleDao;
import com.sergey.zhuravlev.auctionserver.dao.UserDao;
import com.sergey.zhuravlev.auctionserver.model.Role;
import com.sergey.zhuravlev.auctionserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleDao.getOne(1L));
        user.setRoles(roles);
        userDao.save(user);
    }

    @Override
    public void update(User user){
        userDao.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByUsernameWithoutPassword(String username) {
        User user = userDao.findByUsername(username);
        if (user != null) user.setPassword("");
        return user;
    }

    @Override
    public User getUserWithoutPassword(Long id) {
        User user = userDao.findOne(id);
        user.setPassword("");
        return user;
    }

    @Override
    public User getOne(Long userID) {
        return userDao.findOne(userID);
    }

    @Override
    public void deleteNotificationToken(Long id) {
        userDao.deleteNotificationToken(id);
    }

}
