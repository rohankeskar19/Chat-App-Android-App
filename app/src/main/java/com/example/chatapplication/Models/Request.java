package com.example.chatapplication.Models;

public class Request {
    String id;
    String fromId;
    String fromUsername;
    String fromProfileUrl;


    public Request() {
    }

    public Request(String id, String fromId, String fromUsername, String fromProfileUrl) {
        this.id = id;
        this.fromId = fromId;
        this.fromUsername = fromUsername;
        this.fromProfileUrl = fromProfileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getFromProfileUrl() {
        return fromProfileUrl;
    }

    public void setFromProfileUrl(String fromProfileUrl) {
        this.fromProfileUrl = fromProfileUrl;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", fromId='" + fromId + '\'' +
                ", fromUsername='" + fromUsername + '\'' +
                ", fromProfileUrl='" + fromProfileUrl + '\'' +
                '}';
    }
}
