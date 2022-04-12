package com.android.zalochat.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class MessageHolder extends RecyclerView.ViewHolder {
    public final ShapeableImageView imgAvatarMessage;
    public final TextView tvMessageContent;
    public MessageHolder(@NonNull View view){
        super(view);
        this.tvMessageContent = view.findViewById(R.id.tvMessageContent);
        this.imgAvatarMessage = view.findViewById(R.id.imgAvatarMessage);
    }
}
