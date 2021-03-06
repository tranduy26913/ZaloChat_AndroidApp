package com.android.zalochat.model;

import java.io.Serializable;

public class User implements Serializable { // Đối tượng user
    protected String userId; // User id
    protected String phone; // số điện thoại của người dùng
    protected String password; // password của người dùng
    protected String fullname = ""; // tên người dùng
    protected String avatar = "https://firebasestorage.googleapis.com/v0/b/zalo-3bea3.appspot.com/o/IMAGES%2Favatar.jpg?alt=media&token=7260c6b2-c99d-4e1f-a21e-e26571cb5b15"; // avatar mặc định
    protected boolean active = false; //trạng thái tài khoản
    protected boolean online = true; // trạng thái người dùng

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
