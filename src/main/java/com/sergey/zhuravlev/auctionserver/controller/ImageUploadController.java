package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.service.ImageStorageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Log
@Controller
public class ImageUploadController {

    private final ImageStorageService storageService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public ImageUploadController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> get(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, servletContext.getMimeType(filename))
                .body(file);
    }

    @PostMapping("/images")
    public ResponseEntity upload(@RequestParam MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String savedPath = storageService.save(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/images/" + savedPath);
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

}
