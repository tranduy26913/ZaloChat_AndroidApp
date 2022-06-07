package com.android.zalochat.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.google.android.material.imageview.ShapeableImageView;

public class SearchHolder extends RecyclerView.ViewHolder {
    public final TextView tvDisplayNameSearch;//Liên kết tới TextView tvDisplayNameSearch trên layout
    public final Button btnStartChat;//Liên kết tới Button btnStartChat trên layout
    public final ShapeableImageView imgAvatarSearch;//Liên kết tới phần tử ShapeableImageView imgAvatar trên layout
    public SearchHolder(@NonNull View itemView) {
        super(itemView);
        this.tvDisplayNameSearch = itemView.findViewById(R.id.tvDisplayNameSearch);//Gắn tvDisplayNameSearch trên layout cho biến
        this.btnStartChat = itemView.findViewById(R.id.btnStartChat);//Gắn btnStartChat trên layout cho biến
        this.imgAvatarSearch =itemView.findViewById(R.id.imgAvatarSearch);//gắn imgAvatarSearch trên layout cho biến
    }
}
