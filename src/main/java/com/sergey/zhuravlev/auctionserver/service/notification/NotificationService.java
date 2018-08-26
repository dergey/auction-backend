package com.sergey.zhuravlev.auctionserver.service.notification;

import com.sergey.zhuravlev.auctionserver.repository.UserRepository;
import com.sergey.zhuravlev.auctionserver.service.notification.type.Notification;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Log
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final String firebaseServerKey;

    @Autowired
    public NotificationService(@Value("${firebase.server.key}") String firebaseServerKey, UserRepository userRepository) {
        this.firebaseServerKey = firebaseServerKey;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init(){
        log.info("Все пользователи были сброшены");
        userRepository.deleteAllNotificationTokens();
    }

    public void send(Notification notification) {
        if (notification.toNotNull()) {
            log.info("JSON : " + notification.toJSON());
            HttpEntity<String> request = new HttpEntity<>(notification.toJSON().toString());
            ThreadSender sender = new ThreadSender(firebaseServerKey);
            sender.init(request);
        } else
            log.info("is NULL");
    }
    
}
