package com.example.chatapplication.Models;



public class Conversation {


    String conversationId;
    String userId;
    String username;
    String name;
    String userProfileUrl;
    String lastMessageSenderId;
    String lastMessage;
    String lastMessageTimeStamp;
    String lastMessageDate;
    String conversationOpened;
    String createdAt;

    public Conversation(String conversationId, String userId, String username, String name, String userProfileUrl, String lastMessageSenderId, String lastMessage, String lastMessageTimeStamp, String lastMessageDate, String conversationOpened, String createdAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.userProfileUrl = userProfileUrl;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageDate = lastMessageDate;
        this.conversationOpened = conversationOpened;
        this.createdAt = createdAt;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(String lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getConversationOpened() {
        return conversationOpened;
    }

    public void setConversationOpened(String conversationOpened) {
        this.conversationOpened = conversationOpened;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationId='" + conversationId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", userProfileUrl='" + userProfileUrl + '\'' +
                ", lastMessageSenderId='" + lastMessageSenderId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTimeStamp='" + lastMessageTimeStamp + '\'' +
                ", lastMessageDate='" + lastMessageDate + '\'' +
                ", conversationOpened='" + conversationOpened + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
