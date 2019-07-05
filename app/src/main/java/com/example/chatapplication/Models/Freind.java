package com.example.chatapplication.Models;

import java.io.Serializable;

public class Freind implements Serializable {
    String id;
    String username;
    String profileUrl;
    String name;

    public Freind() {
    }

    public Freind(String id, String username, String profileUrl, String name) {
        this.id = id;
        this.username = username;
        this.profileUrl = profileUrl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Freind{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
