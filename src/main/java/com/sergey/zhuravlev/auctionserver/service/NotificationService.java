package com.sergey.zhuravlev.auctionserver.service;

import com.sergey.zhuravlev.auctionserver.entity.*;
import com.sergey.zhuravlev.auctionserver.enums.NotificationType;
import com.sergey.zhuravlev.auctionserver.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(NotificationType type, Account account) {
        Notification notification = new Notification(null, type, "","", new Date(), account, false);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationForAccount(Account account) {
        return notificationRepository.getAllByRecipient(account);
    }

    @Transactional
    public Notification createNotificationBidBroken(Bid bid) {
        Date now = new Date();
        Notification notification = new Notification(null,
                NotificationType.BID_BROKEN,
                "Bid broken",
                "Bid on %s lot broken",
                now,
                bid.getOwner(),
                false);
        //log.debug("SEND MESSAGE : buyer - " + currentBid.getBuyer().getUsername() + " new bid size - " + bid.getSize() + " lot " + lot);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotificationNewBid(Bid bid) {
        Date now = new Date();
        Notification notification = new Notification(null,
                NotificationType.NEW_BID,
                "",
                "",
                now,
                bid.getLot().getOwner(),
                false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotificationLotExpired(Lot lot) {
        Date now = new Date();
        Notification notification = new Notification(null,
                NotificationType.LOT_EXPIRED,
                "",
                "",
                now,
                lot.getOwner(),
                false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotificationLotSold(Lot lot) {
        Date now = new Date();
        Notification notification = new Notification(null,
                NotificationType.LOT_SOLD,
                "",
                "",
                now,
                lot.getOwner(),
                false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotificationLotPurchased(Lot lot) {
        Date now = new Date();
        Notification notification = new Notification(null,
                NotificationType.LOT_PURCHASED,
                "",
                "",
                now,
                lot.getCurrentBid().getOwner(),
                false);
        return notificationRepository.save(notification);
    }

}
