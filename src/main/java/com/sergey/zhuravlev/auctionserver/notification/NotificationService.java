package com.sergey.zhuravlev.auctionserver.notification;

import com.sergey.zhuravlev.auctionserver.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NotificationService {

    @Autowired
    private UserDao users;

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    @PostConstruct
    public void init(){
        logger.debug("Все пользователи были сброшены");
        users.deleteAllNotificationTokens();
    }

    public void send(Notification notification) {
        if (notification.toNotNull()) {
            logger.debug("JSON : " + notification.toJSON().toString());
            HttpEntity<String> request = new HttpEntity<>(notification.toJSON().toString());
            ThreadSender sender = new ThreadSender();
            sender.init(request);
        } else logger.debug("is NULL");
    }
    
}
