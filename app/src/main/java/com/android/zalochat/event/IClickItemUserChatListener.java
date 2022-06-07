package com.android.zalochat.event;

import com.android.zalochat.model.payload.UserChat;

public interface IClickItemUserChatListener {//Interface xử lý sự kiện click item UserChat
    void onClickItemUserChat(UserChat userChat);
}
