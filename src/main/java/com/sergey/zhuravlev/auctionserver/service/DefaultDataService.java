package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DefaultDataService {

    private final CategoryService categoryService;
    private final ImageService imageService;

    @PostConstruct
    @Transactional
    void initialize() throws IOException {
        if (categoryService.list().isEmpty()) {
            ClassPathResource resource = new ClassPathResource("images/category_all.png");
            Image image = imageService.save(IOUtils.toByteArray(resource.getInputStream()));
            categoryService.createCategory("Other", image);
        }
    }

}
