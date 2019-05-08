package com.sergey.zhuravlev.auctionserver.security.service;

import com.sergey.zhuravlev.auctionserver.security.config.AppProperties;
import com.sergey.zhuravlev.auctionserver.security.user.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class TokenProviderService {

    private final AppProperties appProperties;

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty.", ex);
        }
        return false;
    }

}
