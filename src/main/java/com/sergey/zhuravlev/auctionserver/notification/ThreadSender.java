package com.sergey.zhuravlev.auctionserver.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

public class ThreadSender extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);
    private static final String FIREBASE_SERVER_KEY = "AIzaSyAWhkclDhjqJ0CZBuYB9N2GETP1LqdyxK8";

    private RestTemplate restTemplate;
    private HttpEntity request;

    public void init(HttpEntity request){
        this.request = request;
        restTemplate = new RestTemplate();
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json;charset=UTF-8"));
        restTemplate.setInterceptors(interceptors);
        this.start();
    }

    @Override
    public void run() {
        FirebaseResponse firebaseResponse = restTemplate.postForObject("https://fcm.googleapis.com/fcm/send", request, FirebaseResponse.class);
        logger.debug(firebaseResponse.toString());
    }

}
