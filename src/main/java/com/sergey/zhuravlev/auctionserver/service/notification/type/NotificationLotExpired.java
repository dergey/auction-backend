package com.sergey.zhuravlev.auctionserver.service.notification.type;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.entity.Lot;

public class NotificationLotExpired extends Notification {

    private final Lot lot;

    public NotificationLotExpired(Lot lot) {
        super();
        this.to = lot.getOwner().getNotificationToken();
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
