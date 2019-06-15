package com.gjahn.chat.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import javax.websocket.Session;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@EnableWebSocket
@Component
public class ChatSocket extends TextWebSocketHandler implements WebSocketConfigurer {

    private List<WebSocketSession> sessions = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH mm");

    @Override

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this, "/chat").setAllowedOrigins("*");

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sendWelcomeMessage(session);
    }

    private void sendWelcomeMessage(WebSocketSession session) throws Exception {
        for (WebSocketSession session1 : sessions) {
            if (!(session1.getId().equals(session.getId()))) {

                TextMessage textMessage = new TextMessage("<server>SB connected to the chat");
                session1.sendMessage(textMessage);
            }
        }
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        for (WebSocketSession webSocketSession : sessions) {

            TextMessage textMessage = new TextMessage("( "
                    + formatter.format(LocalDateTime.now())
                    + " ): " + message.getPayload()
            );

            webSocketSession.sendMessage(textMessage);
        }
        //   System.out.println("Message: " + message.getPayload());
        // session.sendMessage(message);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
       // System.out.println("<server>SB leave.");
        sendDisconnectMessage(session);
        sessions.remove(session);
    }

    private void sendDisconnectMessage(WebSocketSession session) throws Exception {

        for (WebSocketSession session1 : sessions) {
            if (!(session1.getId().equals(session.getId()))) {

                TextMessage textMessage = new TextMessage("<server>SB leave our chat");
                session1.sendMessage(textMessage);
            }
        }
    }

}