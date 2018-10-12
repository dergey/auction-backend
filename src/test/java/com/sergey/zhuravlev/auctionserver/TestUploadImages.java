package com.sergey.zhuravlev.auctionserver;

import com.sergey.zhuravlev.auctionserver.service.ImageStorageService;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@Log
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Scope("test")
public class TestUploadImages {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImageStorageService storageService;

    @Value("${images.directory}")
    private String directory;

    @Test
    public void shouldUploadFile() {
        ClassPathResource resource = new ClassPathResource("test.jpg", getClass());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        map.add("file", resource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(map, headers);
        ResponseEntity<Object> response = this.restTemplate.postForEntity("/images/", requestEntity, Object.class);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        log.warning("upload to " + response.getHeaders().get(HttpHeaders.LOCATION).get(0));
    }

    @Test
    public void shouldDownloadFile() throws IOException, NoSuchAlgorithmException {
        ClassPathResource resource = new ClassPathResource("test2.jpg", getClass());
        File file = new File(resource.getURI());
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file.jpg",
                file.getName(), "image/jpg", IOUtils.toByteArray(input));
        String gottenFilename = storageService.save(multipartFile);
        ResponseEntity<Void> response = this.restTemplate
                .getForEntity("/images/{filename}", Void.class, gottenFilename);
        assertEquals(response.getStatusCodeValue(), 200);
    }

    @Before
    public void cleanUp() throws IOException {
        URI path = Paths.get(directory).toUri();
        File directory = new File(path);
        FileUtils.deleteDirectory(directory);
    }

}