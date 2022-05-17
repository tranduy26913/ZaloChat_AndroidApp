package com.android.zalochat.model;

import java.io.Serializable;

public class Contact implements Serializable {
    protected String userId;
    protected String phone;
    protected String fullname = "";
    protected String avatar = "";
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
}
