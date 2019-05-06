package com.sergey.zhuravlev.auctionserver.core;

import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebLayerTest {

    private static final String PASSWORD = "fdvsrge4vg5hesgag33klmh3ssdzxfg";

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUserLogin() throws Exception {
        User testLocalUser = userRepository.findAll().get(0);

        RequestBuilder requestBuilder = formLogin()
                .user(testLocalUser.getPrincipal().getEmail())
                .password(PASSWORD);
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JSESSIONID"));
    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

}