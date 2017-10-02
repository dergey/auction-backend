package com.sergey.zhuravlev.auctionserver.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URLEncoder;


public class Notification {

    String ntfTitle, ntfBody, to;
    ObjectNode data, body;
    int type;

    Notification(){
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.createObjectNode();
        body = mapper.createObjectNode();
    }

    public Notification(String title, String body, String to) {
        this();
        this.ntfTitle = title;
        this.ntfBody = body;
        this.type = 0;
        this.to = to;
        this.body.put("to", to);
    }

    public boolean toNotNull(){
        return !(to == null || to.isEmpty());
    }

    public ObjectNode toJSON() {
        try {
            data.put("title", URLEncoder.encode(ntfTitle, "UTF-8"));
            data.put("body",  URLEncoder.encode(ntfBody, "UTF-8"));
            data.put("type", String.valueOf(type));
            body.put("priority", "high");
            body.put("data", data);
        } catch (Exception e){}
        return body;
    }

}
