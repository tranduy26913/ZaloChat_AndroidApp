package com.android.zalochat.adapter;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class UserChatHolder extends RecyclerView.ViewHolder {
    public final ShapeableImageView imgAvatar;
    public final TextView tvDisplayNameUserChat,tvDescriptionMessage;

    public UserChatHolder(@NonNull View itemView){
        super(itemView);
        imgAvatar = itemView.findViewById(R.id.imgAvatarUserChat);
        tvDescriptionMessage = itemView.findViewById(R.id.tvDescriptionMessage);
        tvDisplayNameUserChat = itemView.findViewById(R.id.tvDisplayNameUserChat);
    }
}
