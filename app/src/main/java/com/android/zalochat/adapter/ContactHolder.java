package com.android.zalochat.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.android.zalochat.R;

public class ContactHolder extends RecyclerView.ViewHolder{
    public final ShapeableImageView imgAvatarUserChatPhoneBook;
    public final TextView tvDisplayNameUserChatPhoneBook;
    public final TextView tvDescriptionMessagePhoneBook;
    public final ConstraintLayout layoutInfoUserChatPhoneBook;

    public ContactHolder(@NonNull View view) {
        super(view);
        this.imgAvatarUserChatPhoneBook = view.findViewById(R.id.imgAvatarUserChatPhoneBook);
        this.tvDescriptionMessagePhoneBook = view.findViewById(R.id.tvDescriptionMessagePhoneBook);
        this.tvDisplayNameUserChatPhoneBook = view.findViewById(R.id.tvDisplayNameUserChatPhoneBook);
        this.layoutInfoUserChatPhoneBook = view.findViewById(R.id.layoutInfoUserChatPhoneBook);
    }
}
