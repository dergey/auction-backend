package com.sergey.zhuravlev.auctionserver.notification;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;

public class NotificationLotSold extends Notification {

    private User user;
    private Lot lot;

    public NotificationLotSold(Lot lot){
        super();
        this.user = lot.getOwner();
        this.to = user.getNotificationToken();
        this.type = 4;
        this.lot = lot;
        this.ntfTitle = "Ваш товар был продан";
        this.ntfBody = "Товар " + lot.getTitle();
    }

    @Override
    public ObjectNode toJSON() {
        body.put("to", to);
        data.put("lotId", String.valueOf(lot.getId()));
        return super.toJSON();
    }

}
