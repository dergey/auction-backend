package com.sergey.zhuravlev.auctionserver.notification;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sergey.zhuravlev.auctionserver.model.Bid;
import com.sergey.zhuravlev.auctionserver.model.Lot;
import com.sergey.zhuravlev.auctionserver.model.User;

public class NotificationBetBroken extends Notification {

    private User user;
    private Bid newBid;
    private Lot lot;

    public NotificationBetBroken(User user, Bid newBid, Lot lot){
        super();
        this.to = user.getNotificationToken();
        this.user = user;
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
