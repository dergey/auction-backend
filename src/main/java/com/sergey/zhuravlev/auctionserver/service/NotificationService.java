package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.Notification;
import com.sergey.zhuravlev.auctionserver.entity.User;
import com.sergey.zhuravlev.auctionserver.enums.NotificationType;
import com.sergey.zhuravlev.auctionserver.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(NotificationType type, String title, String body, User user) {
        Notification notification = new Notification(null, type, title, body, user, new Date());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationForUser(Principal user, Long lastReadMessage) {
        //TODO #i
        return null;
    }

}
