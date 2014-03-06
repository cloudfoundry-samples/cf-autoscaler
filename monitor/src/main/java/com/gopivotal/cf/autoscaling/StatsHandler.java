package com.gopivotal.cf.autoscaling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class StatsHandler extends TextWebSocketHandler {

    private Log log = LogFactory.getLog(StatsHandler.class);

    private Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
    }

    public void onStatsReceived(String stats) {
        log.debug("Stats received: " + stats);
        for (WebSocketSession session : sessions.values()) {
            if ((session != null) && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(stats));
                } catch (IOException e) {
                    log.warn("IOException caught when sending message: ", e);
                }
            }
        }
    }
}
