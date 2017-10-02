package com.sergey.zhuravlev.auctionserver.notification;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;
import com.sergey.zhuravlev.auctionserver.thread.LotsThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationLotExpired extends Notification {

    private User user;
    private Lot lot;

    private static final Logger logger = LoggerFactory.getLogger(LotsThread.class);

    public NotificationLotExpired(Lot lot) {
        super();
        this.user = lot.getOwner();
        this.to = user.getNotificationToken();
        this.type = 3;
        this.lot = lot;
        this.ntfTitle = "Товар не был продан";
        this.ntfBody =  "Товар " + lot.getTitle();
    }

    @Override
    public ObjectNode toJSON() {
        body.put("to", to);
        data.put("lotId", String.valueOf(lot.getId()));
        return super.toJSON();
    }

}
