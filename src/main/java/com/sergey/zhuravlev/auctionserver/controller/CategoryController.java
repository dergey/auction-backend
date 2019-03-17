package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.converter.CategoryConverter;
import com.sergey.zhuravlev.auctionserver.dto.CategoryDto;
import com.sergey.zhuravlev.auctionserver.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<CategoryDto> list() {
        return categoryRepository.findAll().stream().map(CategoryConverter::convert).collect(Collectors.toList());
    }

}
