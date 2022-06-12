package com.android.zalochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.model.Contact;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.ChatActivity;
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
        return new ContactHolder(LayoutInflater.from(context).inflate(R.layout.item_user_phone_book,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        Contact contact = contactList.get(position);
        UserChat user = contact.ContactToUserChat();
        holder.tvDescriptionMessagePhoneBook.setText("");
        if(!contact.getAvatar().equals("")){
            Picasso.get().load(contact.getAvatar()).into(holder.imgAvatarUserChatPhoneBook);
        }else{
            holder.imgAvatarUserChatPhoneBook.setImageResource(R.drawable.icon_user);
        }
        holder.tvDisplayNameUserChatPhoneBook.setText(contact.getFullname());
        Log.e("SIZE", "IS USER "+contact.isUser(),null);
        if(contact.isUser()){
            holder.btnStartChatPhoneBook.setVisibility(View.VISIBLE);
            holder.btnStartChatPhoneBook.setOnClickListener(new View.OnClickListener() {
                @Override
                //Xử lý sự kiện khi bấm vào item
                public void onClick(View view) {
                    GoToChat(user);
                }
            });
        }
    }

    private void GoToChat(UserChat user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,user);//Truyền thông tin user vào intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Thiết lập flag đánh dấu mở một activity mới
        context.startActivity(intent);//Mở activity
    }

    @Override
    public int getItemCount() {
        if(contactList == null){
            return 0;
        }
        else{
            return contactList.size();
        }
    }
}
