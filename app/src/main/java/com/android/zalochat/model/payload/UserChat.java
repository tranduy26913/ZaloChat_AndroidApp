package com.android.zalochat.model.payload;

public class UserChat {
    private String avatar;
    private String fullname;
    private String message;
    private String phone;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserChat(){}

    public UserChat(String avatar, String fullname, String message, String phone) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.message = message;
        this.phone = phone;
    }

}
