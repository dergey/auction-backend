package com.sergey.zhuravlev.auctionserver.repository;

import com.sergey.zhuravlev.auctionserver.entity.Account;
import com.sergey.zhuravlev.auctionserver.entity.Notification;
import com.sergey.zhuravlev.auctionserver.entity.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> getAllByRecipient(Account account);

}
