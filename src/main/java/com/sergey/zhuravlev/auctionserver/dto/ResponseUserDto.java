package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseUserDto extends UserDto {

    @JsonProperty(value = "id", index = 0)
    private Long id;
    @JsonProperty(value = "rating")
    private Byte rating;

}
