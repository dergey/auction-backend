package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dto.socket.RequestNotification;
import com.sergey.zhuravlev.auctionserver.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class SocketNotificationController {

    private final SimpMessagingTemplate template;
    private final NotificationService notificationService;

    @Autowired
    public SocketNotificationController(SimpMessagingTemplate template, NotificationService notificationService) {
        this.template = template;
        this.notificationService = notificationService;
    }

    @MessageMapping("/api/notification")
    public void sendSpecific(@Payload RequestNotification request, Principal principal) {
//        List<Notification> out = notificationService.getNotificationForUser(principal.get);
//        template.convertAndSendToUser(user.getName(), "/profile/notification", out);
    }

}
