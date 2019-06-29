package com.gjahn.chat.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@EnableWebSocket
@Component
public class ChatSocket extends TextWebSocketHandler implements WebSocketConfigurer {

    // private List<WebSocketSession> sessions = new ArrayList<>();
    //s
    private List<User> users = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH mm");

    @Override

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this, "/chat").setAllowedOrigins("*");

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = new User(session);
        users.add(user);

        user.sendMessage("<server> Welcome on chat, first message became your nickname");
        sendWelcomeMessage(session);
    }

    private void sendWelcomeMessage(WebSocketSession session) throws Exception, NullPointerException {
        for (User user : users) {
            if (!(user.getWebSocketSession().getId().equals(session.getId()))) {

                user.sendMessage("<server>SB connected to the chat");
                TextMessage textMessage = new TextMessage("<server>SB connected to the chat");
                user.getWebSocketSession().sendMessage(textMessage);
            }
        }
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = findUserBySession(session);



        if (user.getNickname() == null) {
            user.setNickname(message.getPayload());
            user.sendMessage("<server> Now you have nickname");
            return;
        }

        for (User user1 : users) {
            String strnigMessage = user.getNickname() + " ("
                    + formatter.format(LocalDateTime.now())
                    + "): " + message.getPayload();
            user.sendMessage(strnigMessage);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sendDisconnectMessage(session);
        users.remove(findUserBySession(session));
    }

    private void sendDisconnectMessage(WebSocketSession session) throws Exception {

        for (User user : users) {
            if (!(user.getWebSocketSession().getId().equals(session.getId()))) {

                TextMessage textMessage = new TextMessage("<server>SB leave our chat");
                user.getWebSocketSession().sendMessage(textMessage);
            }
        }
    }

    private User findUserBySession(WebSocketSession webSocketSession) {
        for (User user : users) {
            if (user.getWebSocketSession().getId().equals(webSocketSession.getId())) {
                return user;
            }
        }
        return null;
    }

    private boolean userHasNickname(WebSocketSession webSocketSession) {

        for (User user : users) {
            if (user.getNickname().equals("")) {
                return false;
            }
            return true;
        }
        return false;

    }

}