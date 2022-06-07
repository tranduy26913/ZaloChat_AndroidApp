package com.android.zalochat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.model.payload.UserChat;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatHolder> {

    private List<UserChat> userChatList;//Danh sách userchat
    private IClickItemUserChatListener iClickItemUserChatListener;//Inteface để callback tới xử lý sự kiện click vào item

    public UserChatAdapter(List<UserChat> userChatList,IClickItemUserChatListener listener) {
        this.userChatList = userChatList;
        this.iClickItemUserChatListener=listener;
    }

    @NonNull
    @Override
    public UserChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat, parent, false);
        return new UserChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatHolder holder, int position) {
        UserChat chat = userChatList.get(position);//Lấy object userchat theo vị trí hiển thị
        Picasso.get().load(chat.getAvatar()).into(holder.imgAvatar);//Load ảnh từ url đưa vào Avatar
        holder.tvDisplayNameUserChat.setText(chat.getFullname());//Gắn giá trị fullname vào TextView DisplayNameUserChat
        holder.tvDescriptionMessage.setText(chat.getMessage());//Gắn giá trị message vào TextView Descripton Message
        holder.itemView.setOnClickListener(new View.OnClickListener() {//Gắn sự kiện onlick khi click vào item
            @Override
            public void onClick(View view) {
                iClickItemUserChatListener.onClickItemUserChat(chat);
            }
        });
    }

    @Override//Trả về số lượng các item
    public int getItemCount() {
        return userChatList.size();
    }


}
