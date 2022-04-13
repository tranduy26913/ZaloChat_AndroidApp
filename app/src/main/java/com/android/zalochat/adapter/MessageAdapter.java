package com.android.zalochat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.model.Message;
import com.android.zalochat.model.payload.MessageChat;
import com.android.zalochat.util.Constants;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {//adapter hiển thị tin nhắn
    private List<MessageChat> messageList;
    private Context context;

    final int MSG_SENDER = 0,MSG_RECEIVER=1;
    public  MessageAdapter(List<MessageChat> messageList,Context context){
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_SENDER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
            return new MessageHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageChat messageChat = messageList.get(position);
        holder.tvMessageContent.setText(messageChat.getMessage().getContent());
        if(position>0){//xử lý trường hợp những tin nhắn liên tiếp sẽ chỉ hiện avatar 1 lần
            if(messageChat.getMessage().getReceiver().equals(messageList.get(position-1).getMessage().getReceiver())){
                holder.imgAvatarMessage.setVisibility(View.INVISIBLE);
            }
            else {
                holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());
            }
        }
        else {
            holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());
        }


    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences ref = context.getSharedPreferences(Constants.SHAREPREF_USER,Context.MODE_PRIVATE);
        String userId= ref.getString(Constants.USERID,"");
        if(messageList.get(position).getMessage().getSender().equals(userId)){
            return MSG_SENDER;
        }
        else return MSG_RECEIVER;
    }
}

