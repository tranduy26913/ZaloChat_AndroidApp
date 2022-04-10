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

    private List<UserChat> userChatList;
    private IClickItemUserChatListener iClickItemUserChatListener;

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
        UserChat chat = userChatList.get(position);
        Picasso.get().load(chat.getAvatar()).into(holder.imgAvatar);
        holder.tvDisplayNameUserChat.setText(chat.getFullname());
        holder.tvDescriptionMessage.setText("Gửi tin nhắn đầu tiên");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemUserChatListener.onClickItemUserChat(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userChatList.size();
    }


}
