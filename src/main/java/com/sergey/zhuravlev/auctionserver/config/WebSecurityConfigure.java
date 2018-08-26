package com.sergey.zhuravlev.auctionserver.config;

import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.security.MySavedRequestAwareAuthenticationSuccessHandler;
import com.sergey.zhuravlev.auctionserver.security.RestAuthenticationEntryPoint;
import com.sergey.zhuravlev.auctionserver.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final MySavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    public WebSecurityConfigure(UserRepository userRepository,
                                RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                MySavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler) {
        super();
        this.userRepository = userRepository;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                userDetailsService(userDetailsService()).
                passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                    .formLogin()
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                    .httpBasic()
                .and()
                    .logout();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

}