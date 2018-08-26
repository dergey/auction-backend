package com.sergey.zhuravlev.auctionserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionDto {

    private Date timestamp;
    private String message;
    private String details;

}