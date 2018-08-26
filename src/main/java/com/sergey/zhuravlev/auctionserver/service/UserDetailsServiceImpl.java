package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.entity.Role;
import com.sergey.zhuravlev.auctionserver.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserRepositoryUserDetails(user);
    }

    final static class UserRepositoryUserDetails extends User implements UserDetails {

        private final Set<GrantedAuthority> grantedAuthorities;

        private UserRepositoryUserDetails(User user) {
            super(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), user.getFirstname(),
                    user.getLastname(), user.getEmail(), user.getRating(), user.getHistory(), user.getNotificationToken());
            grantedAuthorities = new HashSet<>();
            for (Role role : this.getRoles()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return grantedAuthorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

}
