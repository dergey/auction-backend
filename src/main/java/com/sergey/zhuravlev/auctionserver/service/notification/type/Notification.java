package com.sergey.zhuravlev.auctionserver.service.notification.type;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URLEncoder;


public class Notification {

    String ntfTitle, ntfBody, to;
    final ObjectNode data;
    final ObjectNode body;
    int type;

    Notification() {
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.createObjectNode();
        body = mapper.createObjectNode();
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
            body.set("data", data);
        } catch (Exception e){
            e.printStackTrace();
        }
        return body;
    }

}
