package com.example.chatapplication.Models;

public class Message {

    String id;

    String conversationId;

    String fromId;

    String toId;

    String content;

    String createdAt;

    String time;

    public Message(String id, String conversationId, String fromId, String toId, String content, String createdAt, String time) {
        this.id = id;
        this.conversationId = conversationId;
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
        this.createdAt = createdAt;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
