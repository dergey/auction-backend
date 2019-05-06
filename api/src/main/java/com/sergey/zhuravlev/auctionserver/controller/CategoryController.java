package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.CategoryConverter;
import com.sergey.zhuravlev.auctionserver.core.service.CategoryService;
import com.sergey.zhuravlev.auctionserver.dto.CategoryDto;
import com.sergey.zhuravlev.auctionserver.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public PageDto<CategoryDto> list(@RequestParam(value = "size", required = false) Integer size,
                                     @RequestParam(value = "page", required = false) Integer page) {
        return new PageDto<>(categoryService.list(PageRequest.of(
                page == null ? 0 : page,
                size == null ? PageDto.DEFAULT_PAGE_SIZE : size))
                .map(CategoryConverter::convert));
    }

}
