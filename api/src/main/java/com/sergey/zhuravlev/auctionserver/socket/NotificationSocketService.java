package com.sergey.zhuravlev.auctionserver.socket;

import com.sergey.zhuravlev.auctionserver.converter.NotificationConverter;
import com.sergey.zhuravlev.auctionserver.core.service.AccountService;
import com.sergey.zhuravlev.auctionserver.core.service.NotificationService;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Notification;
import com.sergey.zhuravlev.auctionserver.dto.socket.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationSocketService {

    private final AccountService accountService;
    private final NotificationService notificationService;

    private final SimpMessagingTemplate template;

    @Transactional(readOnly = true)
    public boolean sendNotification(Notification notification) {
        NotificationResponseDto notificationResponseDto = NotificationConverter.getNotificationResponseDto(notification);
        template.convertAndSendToUser(notification.getRecipient().getUsername(), "/queue/notification", notificationResponseDto);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean sendNotification(Notification notification, Account recipient) {
        NotificationResponseDto notificationResponseDto = NotificationConverter.getNotificationResponseDto(notification);
        template.convertAndSendToUser(recipient.getUsername(), "/queue/notification", notificationResponseDto);
        return true;
    }

    @Transactional(readOnly = true)
    public void sendNotificationHistory(Date lastConnection, String username) {
        Account account = accountService.getAccountByUsername(username);
        List<Notification> notifications = notificationService.getNotificationForAccountAfterDate(account, lastConnection);
        log.info("Sent {} notifications", notifications.size());
        for (Notification notification : notifications) {
            sendNotification(notification, account);
        }
    }
}
