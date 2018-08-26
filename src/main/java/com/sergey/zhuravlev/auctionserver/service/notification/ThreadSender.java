package com.sergey.zhuravlev.auctionserver.service.notification;

import lombok.extern.java.Log;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Objects;

@Log
class ThreadSender extends Thread {

    private final String firebaseServerKey;

    private RestTemplate restTemplate;
    private HttpEntity request;

    ThreadSender(String firebaseServerKey) {
        this.firebaseServerKey = firebaseServerKey;
    }

    void init(HttpEntity request){
        this.request = request;
        restTemplate = new RestTemplate();
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + firebaseServerKey));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json;charset=UTF-8"));
        restTemplate.setInterceptors(interceptors);
        this.start();
    }

    @Override
    public void run() {
        FirebaseResponse firebaseResponse =
                restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);
        log.info(Objects.requireNonNull(firebaseResponse).toString());
    }

}
