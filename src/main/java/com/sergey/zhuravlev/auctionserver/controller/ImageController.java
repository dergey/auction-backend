package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.entity.Image;
import com.sergey.zhuravlev.auctionserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("{name:.+}")
    @ResponseBody
    public HttpEntity<byte[]> download(@PathVariable String name) {
        Image image = imageService.getImage(name);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setContentType(MediaType.valueOf(image.getContentType()));
        return new ResponseEntity<>(image.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity upload(@RequestParam MultipartFile file) {
        Image image = imageService.save(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/images/" + image.getName());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

}
