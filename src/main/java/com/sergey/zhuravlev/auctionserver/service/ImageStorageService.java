package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.exception.NotFoundException;
import lombok.extern.java.Log;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log
@Service
public class ImageStorageService {

    private Path rootLocation;

    public ImageStorageService(@Value("${images.directory}") String dir) {
        this.rootLocation = Paths.get(dir);
        log.info("Images storage location is " + rootLocation.toAbsolutePath());
    }

    public String save(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        if (file.isEmpty()) {
            throw new RuntimeException("Empty file!");
        }

        String filename = md5hash(file.getBytes()) + "." + FilenameUtils.getExtension(file.getOriginalFilename());

        log.info("Save file " + filename);

        try (InputStream inputStream = file.getInputStream()) {
            Path target = rootLocation.resolve(filename).toAbsolutePath();
            Files.createDirectories(target);
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException("Already Exists " + filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    private String md5hash(byte[] uploadBytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(uploadBytes);
        return new BigInteger(1, digest).toString(16);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException(String.format("File %s not found!", filename));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

}
