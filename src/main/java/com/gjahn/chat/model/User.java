package com.gjahn.chat.model;

import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

@Data
public class User {

    private String nickname;
    private WebSocketSession webSocketSession;
    private String lastMessTime;


    public User (WebSocketSession webSocketSession){
        this.webSocketSession = webSocketSession;
    }

    public void sendMessage (String message) throws IOException{
        webSocketSession.sendMessage(new TextMessage(message));
    }

  /*  public boolean timeCheck (String timeToCheck) {


        Date data1 = format.parse (timeToCheck);
       Date date2 = format.parse (lastMessTime);

        long difference = date2.getTime() - data1.getTime();


        if (diference<1000 ){
            return true;
        }
        return false;
    }*/
}
