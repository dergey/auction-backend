package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findByPrincipalEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
        return UserPrincipal.create(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
        return UserPrincipal.create(user);
    }

}
