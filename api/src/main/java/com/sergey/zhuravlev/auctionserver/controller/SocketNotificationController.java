package com.sergey.zhuravlev.auctionserver.controller;

import com.sergey.zhuravlev.auctionserver.dto.socket.NotificationRequestDto;
import com.sergey.zhuravlev.auctionserver.socket.NotificationSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Log4j2
@RestController
@RequiredArgsConstructor
public class SocketNotificationController {

    private final NotificationSocketService notificationSocketService;

    @MessageMapping("/notification")
    public void requestNotification(@Payload NotificationRequestDto notificationRequestDto, Principal principal) {
        log.info("Request notification for {} user after {}.", principal.getName(), notificationRequestDto.getLastConnection());
        notificationSocketService.sendNotificationHistory(notificationRequestDto.getLastConnection(), principal.getName());
    }

//    @MessageMapping("/hello-msg-mapping")// сюда тип отправляем ... <---
//    @SendTo("/topic/greetings")//отсюда тип принимаем ... <---
//    MessageResponseDto echoMessageMapping(String message) {
//        log.info("React to hello-msg-mapping");
//        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//
//        return new MessageResponseDto(username, message, time);
//    }

//    @Autowired
//    public SocketNotificationController(SimpMessagingTemplate template, NotificationService notificationService) {
//        this.template = template;
//        this.notificationService = notificationService;
//    }
//
//    @MessageMapping("/notification")
//    public void sendSpecific(@Payload NotificationRequestDto request, Principal principal) {
//
//        List<Notification> out = notificationService.getNotificationForAccount();
//        template.convertAndSendToUser(user.getName(), "/profile/notification", out);
//    }

}
