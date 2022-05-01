package com.android.zalochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
    List<UserChat> userList;
    Context context;

    public SearchAdapter(Context context,List<UserChat> userList){
        this.userList = userList;
        this.context = context;
    }
    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_search_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        UserChat user =userList.get(position);
        try{
            holder.tvDisplayNameSearch.setText(user.getFullname());
            holder.btnStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoToChat(user);
                }
            });
            Picasso.get().load(user.getAvatar()).into(holder.imgAvatarSearch);
        }catch (Exception ex){

        }
    }

    private void GoToChat(UserChat user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
