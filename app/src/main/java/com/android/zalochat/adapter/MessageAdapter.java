package com.android.zalochat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.model.payload.MessageChat;
import com.android.zalochat.util.Constants;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {//adapter hiển thị tin nhắn
    private List<MessageChat> messageList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String chatId;
    String pattern = "HH:mm";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    final int MSG_SENDER = 0, MSG_RECEIVER = 1;

    public MessageAdapter(List<MessageChat> messageList, Context context, String chatId) {
        this.messageList = messageList;
        this.context = context;
        this.chatId = chatId;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageChat messageChat = messageList.get(position);

        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            if (pos < 0)
                return false;
            if (pos == messageChat.getMessage().getReaction())
                pos = -1;
            if (pos == -1) {
                holder.imgReaction.setImageResource(R.drawable.ic_like_border);
            } else {
                holder.imgReaction.setImageResource(reactions[pos]);
            }


            messageChat.getMessage().setReaction(pos);

            db.collection(Constants.MESSAGE_COLLECTION)
                    .document(chatId)
                    .collection(Constants.SUBMESSAGE_COLLECTION)
                    .document(messageChat.getMessage().getId()).update("reaction", pos);


            return true; // true is closing popup, false is requesting a new selection
        });


        holder.imgReaction.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popup.onTouch(view, motionEvent);
                return false;
            }
        });

        if (position > 0) {//xử lý trường hợp những tin nhắn liên tiếp sẽ chỉ hiện avatar 1 lần
            if (messageChat.getMessage().getReceiver().equals(messageList.get(position - 1).getMessage().getReceiver())) {
                holder.imgAvatarMessage.setVisibility(View.INVISIBLE);
            } else {
                holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());
            }
        } else {
            holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());
        }
        Date date = new Date(messageChat.getMessage().getTime());
        if (position < messageList.size()) {
            if (position == messageList.size() - 1) {
                holder.tvMessageTime.setText(simpleDateFormat.format(date));
            } else {
                if (!messageList.get(position).getMessage().getSender().equals(messageList.get(position + 1).getMessage().getSender())) {
                    holder.tvMessageTime.setText(simpleDateFormat.format(date));
                } else {
                    holder.tvMessageTime.setVisibility(View.GONE);
                }
            }
        }

        if (messageChat.getMessage().getType().equals(Constants.IMAGE)) {
            holder.tvMessageContent.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.VISIBLE);
            holder.layoutMessageChatContent.setVisibility(View.GONE);
            Picasso.get().load(messageChat.getMessage().getContent()).into(holder.imgMessage);

            //Issue: Hình ảnh quá nặng sẽ bị reloading khi recyclerView load lại
        } else {
            holder.imgMessage.setVisibility(View.GONE);
            holder.tvMessageContent.setText(messageChat.getMessage().getContent());
        }
        if (messageChat.getMessage().getReaction() != -1)
            holder.imgReaction.setImageResource(reactions[messageChat.getMessage().getReaction()]);


    }


    @Override
    public int getItemCount() {
        if (messageList == null)
            return 0;
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences ref = context.getSharedPreferences(Constants.SHAREPREF_USER, Context.MODE_PRIVATE);
        String userId = ref.getString(Constants.USERID, "");
        if (messageList.get(position).getMessage().getSender().equals(userId)) {
            return MSG_SENDER;
        } else return MSG_RECEIVER;
    }
}

