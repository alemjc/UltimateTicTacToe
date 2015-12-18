package com.games.ultimatetictactoe.app;

/**
 * Created by alemjc on 12/13/15.
 */
public class User {
    private String userName;
    private String msgId;

    public User(String userName, String msgId){
        this.userName = userName;
        this.msgId = msgId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
