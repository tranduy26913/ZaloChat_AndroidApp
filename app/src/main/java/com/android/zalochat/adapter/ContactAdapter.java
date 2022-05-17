package com.android.zalochat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
    private List<Contact> contactList;
    private Context context;

    public ContactAdapter(Context context, List<Contact> contactList){
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvDescriptionMessagePhoneBook.setText("");
        if(contact.getAvatar()!= null){
            Picasso.get().load(contact.getAvatar()).into(holder.imgAvatarUserChatPhoneBook);
        }
        holder.tvDisplayNameUserChatPhoneBook.setText(contact.getFullname());
    }

    @Override
    public int getItemCount() {
        if(contactList != null){
            return 0;
        }
        else{
            return contactList.size();
        }
    }
}
