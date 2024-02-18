package com.guauapp.model;

import java.util.List;

public class Chatroom {
    String chatroomId;
    List<String> usersId;
    long lastMessageTimestamp;
    String lastMessageSenderId;

    public Chatroom() {
    }

    public Chatroom(String chatroomId, List<String> usersId, long lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.usersId = usersId;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        this.usersId = usersId;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
