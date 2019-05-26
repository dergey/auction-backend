package com.sergey.zhuravlev.auctionserver.database.repository;

import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> getAllByRecipientAndCreateAtAfter(Account account, Date date);

}
