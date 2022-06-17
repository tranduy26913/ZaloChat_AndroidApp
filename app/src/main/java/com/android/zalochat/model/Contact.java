package com.android.zalochat.model;

import com.android.zalochat.model.payload.UserChat;

import java.io.Serializable;

public class Contact implements Serializable { // Đối tượng đại diện cho danh bạ của người dùng, không có trên database, đối tượng này sẽ tương tự với user nhưng sẽ không có một vài
    // trường (field)
    protected String userId;
    protected String phone;
    protected String fullname = "";
    protected String avatar = "";
    protected boolean isUser = false;
    protected boolean active = false;
    protected boolean online = true;

    public Contact() {

    }

    public Contact(String userId, String phone, String fullname, String avatar) {
        this.userId = userId;
        this.phone = phone;
        this.fullname = fullname;
        this.avatar = avatar;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

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

    public UserChat ContactToUserChat(){ // ép kiểu từ Contact sang UserChat để phục vụ cho chức năng nhắn tin
        UserChat user = new UserChat();
        user.setFullname(this.getFullname());
        user.setAvatar(this.getAvatar());
        user.setUserId(this.getUserId());
        user.setPhone(this.getPhone());
        user.setChatId("");
        user.setMessage("");
        return user;
    }
}
