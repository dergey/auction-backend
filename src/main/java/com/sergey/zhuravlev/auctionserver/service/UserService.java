package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.RoleRepository;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.entity.Role;
import com.sergey.zhuravlev.auctionserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public User get(Long userID) {
        return userRepository.getById(userID);
    }

    @Transactional
    public User create(String username, String rawPassword, String firstname, String lastname,
                       String email, String history) {
        Role userRole = roleRepository.getByName("ROLE_USER");
        Set<Role> roles = Collections.singleton(userRole);
        String password = passwordEncoder.encode(rawPassword);
        User user = new User(null, username, password, roles, firstname, lastname,
                email, (byte) 0, history);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(Long id, String username, String firstname, String lastname,
                       String email, String history){
        User user = userRepository.getById(id);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setHistory(history);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void deleteNotificationToken(Long id) {
        userRepository.deleteNotificationToken(id);
    }

    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new NotFoundException(String.format("User with name %s not found", username));
        return user;
    }

}
