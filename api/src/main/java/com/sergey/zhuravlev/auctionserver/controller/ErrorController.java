package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.dto.ExceptionDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import java.util.Date;

@Log4j2
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> accessDeniedHandle(RuntimeException ex, WebRequest request) {
        log.warn("Access denied", ex);
        ExceptionDto error = new ExceptionDto(new Date(),  "Forbidden", request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, NotFoundException.class})
    public ResponseEntity<ExceptionDto> notFoundHandle(RuntimeException ex, WebRequest request) {
        log.warn("Not found", ex);
        ExceptionDto error = new ExceptionDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ExceptionDto> unexpectedSystemErrorHandle(RuntimeException ex, WebRequest request) {
        log.warn("Unexpected system error", ex);
        ExceptionDto error = new ExceptionDto(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}