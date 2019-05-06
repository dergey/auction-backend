package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.entity.Category;
import com.sergey.zhuravlev.auctionserver.database.entity.Image;
import com.sergey.zhuravlev.auctionserver.database.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Category> list(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Category getCategory(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", categoryId)));
    }

    @Transactional
    public Category createCategory(String name, Image image) {
        Category category = new Category(null, name, image);
        category = categoryRepository.save(category);
        return category;
    }

    @Transactional
    public Category updateCategory(Long categoryId, String name, Image image) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", categoryId)));
        category.setName(name);
        category.setImage(image);
        category = categoryRepository.save(category);
        return category;
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id %s not found", categoryId)));
        categoryRepository.delete(category);
    }

}
