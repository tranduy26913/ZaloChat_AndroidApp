package com.android.zalochat.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class SearchHolder extends RecyclerView.ViewHolder {
    public final TextView tvDisplayNameSearch;
    public final Button btnStartChat;
    public final ShapeableImageView imgAvatarSearch;
    public SearchHolder(@NonNull View itemView) {
        super(itemView);
        this.tvDisplayNameSearch = itemView.findViewById(R.id.tvDisplayNameSearch);
        this.btnStartChat = itemView.findViewById(R.id.btnStartChat);
        this.imgAvatarSearch =itemView.findViewById(R.id.imgAvatarSearch);
    }
}
