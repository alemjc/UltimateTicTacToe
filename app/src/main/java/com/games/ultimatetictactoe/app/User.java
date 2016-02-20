package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 12/13/15.
 */
public class User {
    private String registrationTime;
    private String senderId;

    public User(String senderId, String registrationTime){
        this.senderId = senderId;
        this.registrationTime = registrationTime;
    }


    public String getSenderId() {
        return senderId;
    }

    public String getRegistrationTime(){return registrationTime;}


}
