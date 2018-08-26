package com.sergey.zhuravlev.auctionserver.service.notification.type;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.entity.Bid;
import com.sergey.zhuravlev.auctionserver.entity.Lot;
import com.sergey.zhuravlev.auctionserver.entity.User;

public class NotificationBetBroken extends Notification {

    private final Bid newBid;
    private final Lot lot;

    public NotificationBetBroken(User user, Bid newBid, Lot lot){
        super();
        this.to = user.getNotificationToken();
        this.type = 1;
        this.newBid = newBid;
        this.lot = lot;
        this.ntfTitle = "Ставка была побита";
        this.ntfBody = "Новая ставка на товар " + lot.getTitle();
    }

    @Override
    public ObjectNode toJSON() {
        body.put("to", to);
        data.put("lotId", String.valueOf(lot.getId()));
        data.put("newSize", String.valueOf(newBid.getSize()));
        return super.toJSON();
    }
}
