package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Notification;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.NotificationType;
import com.sergey.zhuravlev.auctionserver.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification createNotification(NotificationType type, String title, String body, User user) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setUser(user);
        notification.setCreateTime(new Date());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationForUser(Principal user, Long lastReadMessage) {
        return null;
    }

}
