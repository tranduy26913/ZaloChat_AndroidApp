package com.android.zalochat.mapping;

import android.graphics.Bitmap;

import com.android.zalochat.model.Message;
import com.android.zalochat.model.payload.MessageChat;

public class MessageMapping {
    public static final MessageChat EntityToMessageChat(Message message, Bitmap avatar){
        MessageChat messageChat = new MessageChat();
        messageChat.setMessage(message);
        messageChat.setAvatar(avatar);
        return messageChat;
    }
}
