package com.sergey.zhuravlev.auctionserver.core;

import com.sergey.zhuravlev.auctionserver.core.service.ImageService;
import com.sergey.zhuravlev.auctionserver.database.entity.Image;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@Log
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Scope("test")
public class TestUploadImages {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImageService storageService;

    @Test
    public void shouldUploadFile() {
        ClassPathResource resource = new ClassPathResource("test.jpg", getClass());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        map.add("file", resource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = this.restTemplate.postForEntity("/api/images/", requestEntity, Object.class);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        log.warning("upload to " + response.getHeaders().get(HttpHeaders.LOCATION).get(0));
    }

    @Test
    public void shouldDownloadFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("test2.jpg", getClass());
        File file = new File(resource.getURI());
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file.jpg",
                file.getName(), "image/jpg", IOUtils.toByteArray(input));
        Image image = storageService.save(multipartFile);
        ResponseEntity<Void> response = this.restTemplate
                .getForEntity("/api/images/{filename}", Void.class, image.getName());
        assertEquals(response.getStatusCodeValue(), 200);
    }

}