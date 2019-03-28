package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image getImage(String name) {
        return imageRepository.findByName(name).orElseThrow(() -> new NotFoundException("Image not found"));
    }

    @Transactional
    public Image save(byte[] content) {
        Image image = new Image();
        image.setName(generateName());
        image.setContentType(detectContentType(content));
        image.setContent(content);
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

        try (InputStream inputStream = file.getInputStream()) {
            byte[] content = IOUtils.toByteArray(inputStream);
            image.setContentType(detectContentType(content));
            image.setContent(content);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new BadRequestException("Can't parse requested image");
        }
    }

    private static String detectContentType(byte[] content) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            AutoDetectParser parser = new AutoDetectParser();
            Detector detector = parser.getDetector();
            MediaType mediaType = detector.detect(inputStream, new Metadata());
            return mediaType.toString();
        } catch (IOException ignore) {
            return null;
        }
    }

    private static String generateName() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

}
