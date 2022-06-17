package com.android.zalochat.model.payload;

import java.io.Serializable;

public class UserChat implements Serializable { // Đối tượng này được sử dụng trong trường hợp gọi chức năng chat, không muốn lộ quá nhiều thông tin lên hệ thống, đối tượng này
    // sẽ không có trên database
    private String avatar;
    private String fullname;
    private String message;
    private String phone;
    private String userId;
    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public UserChat(String avatar, String fullname, String message, String phone,String chatId) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.message = message;
        this.phone = phone;
    }

}
