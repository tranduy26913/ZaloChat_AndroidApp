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
    private List<UserChat> userList;//Danh sách user tìm kiếm được
    private Context context;//Context chứa Recycler view

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
        UserChat user =userList.get(position);//lấy userchat theo vị trí hiển thị
        try{
            holder.tvDisplayNameSearch.setText(user.getFullname());//gắn fullname vào DisplayNameSearch
            holder.btnStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                //Xử lý sự kiện khi bấm vào item
                public void onClick(View view) {
                    GoToChat(user);
                }
            });
            Picasso.get().load(user.getAvatar()).into(holder.imgAvatarSearch);//Load hình ảnh từ url vào avatar
        }catch (Exception ex){

        }
    }

    //Mở chat activity
    private void GoToChat(UserChat user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,user);//Truyền thông tin user vào intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Thiết lập flag đánh dấu mở một activity mới
        context.startActivity(intent);//Mở activity
    }

    @Override//Trả về số lượng item
    public int getItemCount() {
        return userList.size();
    }
}
