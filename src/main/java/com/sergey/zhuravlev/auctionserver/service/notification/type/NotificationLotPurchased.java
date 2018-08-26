package com.sergey.zhuravlev.auctionserver.service.notification.type;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;

public class NotificationLotPurchased extends Notification {

    private final Lot lot;

    public NotificationLotPurchased(User user, Lot lot){
        super();
        this.to = user.getNotificationToken();
        this.type = 2;
        this.lot = lot;
        this.ntfTitle = "Вы выйграли в аукционе";
        this.ntfBody = "Товар " + lot.getTitle();
    }

    @Override
    public ObjectNode toJSON() {
        body.put("to", to);
        data.put("lotId", String.valueOf(lot.getId()));
        return super.toJSON();
    }

}
