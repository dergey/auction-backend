package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image getImage(String name) {
        return imageRepository.findByName(name);
    }

    @Transactional
    public Image save(byte[] context) {
        Image image = new Image();
        image.setName(generateName());
        image.setContentType("image/jpg"); //TODO apache tika
        image.setContent(context);
        return imageRepository.save(image);
    }

    @Transactional
    public Image save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Empty file!");
        }

        Image image = new Image();
        image.setName(generateName());
        image.setFilename(file.getOriginalFilename());
        image.setContentType("image/jpg"); //TODO apache tika

        try (InputStream inputStream = file.getInputStream()) {
            byte[] content = IOUtils.toByteArray(inputStream);
            image.setContent(content);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new BadRequestException("Can't parse requested image");
        }
    }

    private static String generateName() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

}
