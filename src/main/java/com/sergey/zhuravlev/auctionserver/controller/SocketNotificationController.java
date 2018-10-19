package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dto.socket.RequestNotification;
import com.sergey.zhuravlev.auctionserver.service.NotificationService;
import com.sergey.zhuravlev.auctionserver.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class SocketNotificationController {

    private final SimpMessagingTemplate template;
    private final NotificationService notificationService;

    @Autowired
    public SocketNotificationController(SimpMessagingTemplate template, NotificationService notificationService) {
        this.template = template;
        this.notificationService = notificationService;
    }

    @MessageMapping(SECURED_CHAT_ROOM)
    public void sendSpecific(@Payload RequestNotification request, Principal user, @Header("simpSessionId") String sessionId) {
        List<Notification> out = notificationService.getNotificationForUser(user, request.getLastReadMessage());
        template.convertAndSendToUser(user, SECURED_CHAT_SPECIFIC_USER, out);
    }

}
