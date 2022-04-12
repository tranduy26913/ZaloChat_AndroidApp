package com.android.zalochat.model.payload;

import android.graphics.Bitmap;

import com.android.zalochat.model.Message;

public class MessageChat {
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
