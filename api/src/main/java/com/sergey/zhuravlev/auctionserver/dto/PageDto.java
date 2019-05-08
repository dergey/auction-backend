package com.sergey.zhuravlev.auctionserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class PageDto<T> {

    public static Integer DEFAULT_PAGE_SIZE = 20;

    @JsonProperty(value = "size")
    private Integer size;

    @JsonProperty(value = "number")
    private Integer number;

    @JsonProperty(value = "total_pages")
    private Integer totalPages;

    @JsonProperty(value = "number_of_elements")
    private Integer numberOfElements;

    @JsonProperty(value = "first")
    private Boolean first;

    @JsonProperty(value = "last")
    private Boolean last;

    @JsonProperty(value = "content")
    private Collection<T> content;

    public PageDto(Page<T> page) {
        this.size = page.getSize();
        this.number = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.content = page.getContent();
    }

}
