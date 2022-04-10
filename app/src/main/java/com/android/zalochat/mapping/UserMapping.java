package com.android.zalochat.mapping;

import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;

import java.util.List;

public class UserMapping {
    public static final UserChat EntityToUserchat(User user, String message){
        UserChat userChat = new UserChat();
        userChat.setAvatar(user.getAvatar());
        userChat.setFullname(user.getFullname());
        userChat.setPhone(user.getPhone());
        userChat.setMessage(message);
        return userChat;
    }
}
