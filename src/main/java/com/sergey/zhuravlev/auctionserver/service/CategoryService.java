package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Category;
import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> list() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(String name, Image image) {
        Category category = new Category(null, name, image);
        category = categoryRepository.save(category);
        return category;
    }

}
