package com.example.chatapplication.Models;

import java.io.Serializable;

public class User implements Serializable {
    String id;
    String name;
    String email;
    String username;
    String profileUrl;
    String bio;
    String createdAt;

    public User() {
    }

    public User(String id, String name, String email, String username, String profileUrl, String bio, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.profileUrl = profileUrl;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
