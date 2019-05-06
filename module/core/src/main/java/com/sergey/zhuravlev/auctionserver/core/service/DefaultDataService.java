package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.database.entity.Image;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
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
        if (!categoryService.list(Pageable.unpaged()).hasContent()) {
            ClassPathResource resource = new ClassPathResource("images/category_all.png");
            Image image = imageService.save(IOUtils.toByteArray(resource.getInputStream()));
            categoryService.createCategory("Other", image);
        }
    }

}
