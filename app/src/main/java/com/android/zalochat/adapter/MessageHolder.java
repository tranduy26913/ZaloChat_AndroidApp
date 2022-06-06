package com.android.zalochat.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class MessageHolder extends RecyclerView.ViewHolder {
    public final ShapeableImageView imgAvatarMessage;
    public final TextView tvMessageContent;
    public final ImageView imgMessage;
    public final ImageView imgReaction;
    public final TextView tvMessageTime;
    public final ConstraintLayout layoutMessageChatContent;
    public MessageHolder(@NonNull View view){
        super(view);
        this.tvMessageContent = view.findViewById(R.id.tvMessageContent);
        this.imgAvatarMessage = view.findViewById(R.id.imgAvatarMessage);
        this.imgMessage = view.findViewById(R.id.imgBodyMessage);
        this.tvMessageTime = view.findViewById(R.id.tvMessageTime);
        this.layoutMessageChatContent  =view.findViewById(R.id.layoutMessageChatContent);
        this.imgReaction = view.findViewById(R.id.imgReactionMessage);
        this.imgReaction.setZ(10);
        this.imgAvatarMessage.setZ(10);
    }
}
