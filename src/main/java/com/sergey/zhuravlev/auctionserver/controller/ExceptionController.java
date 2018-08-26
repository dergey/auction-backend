package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dto.ExceptionDto;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> fieldException(RuntimeException ex, WebRequest request) {
        ExceptionDto error = new ExceptionDto(new Date(),  "Forbidden", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, NotFoundException.class})
    public ResponseEntity<ExceptionDto> entityNotFound(RuntimeException ex, WebRequest request) {
        ExceptionDto error = new ExceptionDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}