package com.sergey.zhuravlev.auctionserver.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Log4j2
@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        if (event.getUser() != null) {
            String username = event.getUser().getName();
            log.info("User {} connected!", username);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        if (event.getUser() != null) {
            String username = event.getUser().getName();
            log.info("User {} disconnected!", username);
        }
    }

}
