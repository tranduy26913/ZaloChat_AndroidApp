package com.android.zalochat.adapter;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class UserChatHolder extends RecyclerView.ViewHolder {
    public final ShapeableImageView imgAvatar;//liên kết tới ShapeableImageView imgAvatar trên layout
    public final TextView tvDisplayNameUserChat,tvDescriptionMessage;//liên kết tới các TextView tvDisplayNameUserChat và tvDescriptionMessage trên layout

    public UserChatHolder(@NonNull View itemView){
        super(itemView);
        imgAvatar = itemView.findViewById(R.id.imgAvatarUserChat);//Gắn phần tử imgAvatarUserChat cho biến
        tvDescriptionMessage = itemView.findViewById(R.id.tvDescriptionMessage);//Gắn phần tử tvDescriptionMessage cho biến
        tvDisplayNameUserChat = itemView.findViewById(R.id.tvDisplayNameUserChat);//Gắn phần tử tvDisplayNameUserChat cho biến
    }
}
