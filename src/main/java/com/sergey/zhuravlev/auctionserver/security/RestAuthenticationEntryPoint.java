package com.sergey.zhuravlev.auctionserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergey.zhuravlev.auctionserver.dto.ExceptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component( "restAuthenticationEntryPoint" )
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(401);
        response.addHeader("Content-Type", "application/json");
        ExceptionDto exceptionDto = new ExceptionDto(new Date(), authException.getMessage(), request.getPathInfo());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, mapper.writeValueAsString(exceptionDto));
    }
}