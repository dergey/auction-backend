package com.sergey.zhuravlev.auctionserver.converter;

import com.sergey.zhuravlev.auctionserver.database.entity.Notification;
import com.sergey.zhuravlev.auctionserver.dto.socket.NotificationResponseDto;

public class NotificationConverter {

    public static NotificationResponseDto getNotificationResponseDto(Notification notification) {
        if (notification == null) return null;
        NotificationResponseDto notificationDto = new NotificationResponseDto();
        notificationDto.setType(notification.getType());
        notificationDto.setCreateAt(notification.getCreateAt());
        notificationDto.setTitle(notification.getTitle());
        notificationDto.setBody(notification.getBody());
        notificationDto.setSend(notification.getSend());
        return notificationDto;
    }

}
