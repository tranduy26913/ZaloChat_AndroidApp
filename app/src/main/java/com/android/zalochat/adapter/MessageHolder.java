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
    public final ShapeableImageView imgAvatarMessage;//Liên kết tới phần tử ShapeableImageView trên layout
    public final TextView tvMessageContent;//Liên kết tới phần tử TextView tvMessageContent trên layout
    public final ImageView imgMessage;//Liên kết tới phần tử ImageView imgMessage trên layout
    public final ImageView imgReaction;//Liên kết tới phần tử ImageView img Reaction trên layout
    public final TextView tvMessageTime;//Liên kết tới phần tử TextView tvMessageTime trên layout
    public final ConstraintLayout layoutMessageChatContent;//Liên kết tới phần tử ContraintLayout trên layout
    public MessageHolder(@NonNull View view){
        super(view);
        this.tvMessageContent = view.findViewById(R.id.tvMessageContent);//Gắn TextView tvMessageContent cho biến
        this.imgAvatarMessage = view.findViewById(R.id.imgAvatarMessage);//Gắn ImageView imgAvatarMessage cho biến
        this.imgMessage = view.findViewById(R.id.imgBodyMessage);//Gắn ImageView Message cho biến
        this.tvMessageTime = view.findViewById(R.id.tvMessageTime);//Gắn TextView tvMessageTime cho biến
        this.layoutMessageChatContent  =view.findViewById(R.id.layoutMessageChatContent);//Gắn layout layoutMessageChatContent cho biến
        this.imgReaction = view.findViewById(R.id.imgReactionMessage);//Gắn ImageView imgReaction cho biến
        this.imgReaction.setZ(10);//Thiết lập Z-index cho imgReaction, để phần tử này nằm trên các phần tử khác
        this.imgAvatarMessage.setZ(10);//Thiết lập Z-index cho imgAvatarMessage, để phần tử này nằm trên các phần tử khác
    }
}
