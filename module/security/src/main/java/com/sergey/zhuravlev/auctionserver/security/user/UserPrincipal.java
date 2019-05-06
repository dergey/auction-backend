package com.sergey.zhuravlev.auctionserver.security.user;

import com.sergey.zhuravlev.auctionserver.database.entity.ForeignUser;
import com.sergey.zhuravlev.auctionserver.database.entity.LocalUser;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.enums.UserType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static UserPrincipal create(LocalUser localUser) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new UserPrincipal(
                localUser.getId(),
                localUser.getPrincipal().getEmail(),
                localUser.getPassword(),
                authorities
        );
    }

    public static UserPrincipal create(User user) {
        if (user.getUserType() == UserType.LOCAL) {
            return create((LocalUser) user);
        } else {
            return create((ForeignUser) user);
        }
    }

    public static UserPrincipal create(ForeignUser foreignUser) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new UserPrincipal(
                foreignUser.getId(),
                foreignUser.getPrincipal().getEmail(),
                null,
                authorities
        );
    }

    public static UserPrincipal create(ForeignUser foreignUser, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(foreignUser);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public String getUsername() {
        return email;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}