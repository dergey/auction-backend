package com.sergey.zhuravlev.auctionserver.aspect;

import com.sergey.zhuravlev.auctionserver.database.entity.Notification;
import com.sergey.zhuravlev.auctionserver.socket.NotificationSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationSocketService notificationSocketService;

    @Pointcut("execution(* com.sergey.zhuravlev.auctionserver.core.service.NotificationService.createNotification*(..))")
    private void createNotificationPointcut(){}

    @AfterReturning(pointcut = "createNotificationPointcut()", returning = "result")
    private void afterNotificationCreate(Object result) {
        Notification notification = (Notification) result;
        notificationSocketService.sendNotification(notification, notification.getRecipient());
    }

}
