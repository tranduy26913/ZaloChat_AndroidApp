package com.android.zalochat.model;

public class User {
    protected String userId;
    protected String phone;
    protected String password;
    protected String fullname = "";
    protected String avatar = "";
    protected boolean active = false;
    protected boolean online = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public User() {

    }

    public User(String phone, String password, String fullname) {
        this.phone = phone;
        this.password = password;
        this.userId = phone;
        this.fullname = fullname;
    }
}
