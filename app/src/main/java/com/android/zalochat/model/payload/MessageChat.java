package com.android.zalochat.model.payload;

import android.graphics.Bitmap;

import com.android.zalochat.model.Message;

public class MessageChat { // Được sử dụng đứa chứa thông tin chat và avatar người gửi, không có trên database
    private Message message;
    private Bitmap avatar;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
}
