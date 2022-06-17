package com.android.zalochat.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.android.zalochat.R;

public class ContactHolder extends RecyclerView.ViewHolder{
    public final ShapeableImageView imgAvatarUserChatPhoneBook; //Chứa hình ảnh người dùng
    public final TextView tvDisplayNameUserChatPhoneBook; //Chứa tên người dùng
    public final TextView tvDescriptionMessagePhoneBook; // Chứa tin nhắn người dùng
    public final ConstraintLayout layoutInfoUserChatPhoneBook; //Constrainlayout
    public final Button btnStartChatPhoneBook; // Button để nhắn tin

    public ContactHolder(@NonNull View view) {
        super(view);
        this.imgAvatarUserChatPhoneBook = view.findViewById(R.id.imgAvatarUserChatPhoneBook); // Ánh xạ view chứa
        this.tvDescriptionMessagePhoneBook = view.findViewById(R.id.tvDescriptionMessagePhoneBook); // Ánh xạ textview
        this.tvDisplayNameUserChatPhoneBook = view.findViewById(R.id.tvDisplayNameUserChatPhoneBook); // Ánh xạ textview
        this.layoutInfoUserChatPhoneBook = view.findViewById(R.id.layoutInfoUserChatPhoneBook); // Ánh xạ constrainlayout
        this.btnStartChatPhoneBook = view.findViewById(R.id.btnStartChatPhoneBook); // Ánh xạ button
    }
}
