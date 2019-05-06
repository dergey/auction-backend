package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.core.service.ImageService;
import com.sergey.zhuravlev.auctionserver.database.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public HttpEntity<Void> upload(@RequestParam MultipartFile file) {
        Image image = imageService.save(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/api/images/" + image.getName());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

}
