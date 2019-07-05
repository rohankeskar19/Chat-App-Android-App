package com.example.chatapplication.Models;

public class UserSearch {
    String id;
    String username;
    String email;
    String profileUrl;
    boolean requestSent;

    public UserSearch() {
    }

    public UserSearch(String id, String username, String email, String profileUrl, boolean requestSent) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.requestSent = requestSent;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isRequestSent() {
        return requestSent;
    }

    public void setRequestSent(boolean requestSent) {
        this.requestSent = requestSent;
    }

    @Override
    public String toString() {
        return "UserSearch{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", requestSent=" + requestSent +
                '}';
    }
}
