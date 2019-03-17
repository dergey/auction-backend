package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.dto.CategoryDto;
import com.sergey.zhuravlev.auctionserver.entity.Category;

public class CategoryConverter {

    public static CategoryDto convert(Category category) {
        if (category == null) return null;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setImage(category.getImage().getName());
        return categoryDto;
    }

}
