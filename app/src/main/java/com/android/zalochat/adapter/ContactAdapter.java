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
    private List<Contact> contactList; //Biến chứa danh sách danh bạ được gửi về từ fragment
    private Context context; //context chứa RecyleView

    public ContactAdapter(Context context, List<Contact> contactList){ //Contructor để tạo contactadapter
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(context).inflate(R.layout.item_user_phone_book,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) { //Xử lý giao diện theo từng dữ liệu trong danh bạ
        Contact contact = contactList.get(position);
        UserChat user = contact.ContactToUserChat();  //Ép kiểu sang UserChat để sử dụng chức năng nhắn tin với người dùng
        holder.tvDescriptionMessagePhoneBook.setText("");
        if(!contact.getAvatar().equals("")){  //Kiểm tra xem dữ liệu người dùng có hình ảnh chưa, chưa có nghĩa là người dùng này không có trong hệ thống và sử dụng hình ảnh default
            Picasso.get().load(contact.getAvatar()).into(holder.imgAvatarUserChatPhoneBook); //Nếu là người dùng trong hệ thống thì sẽ load ảnh của người dùng trên hệ thống ra
        }else{
            holder.imgAvatarUserChatPhoneBook.setImageResource(R.drawable.icon_user); //Set hình ảnh default cho người dùng không có trong hệ thống
        }
        holder.tvDisplayNameUserChatPhoneBook.setText(contact.getFullname()); // Hiển thị tên người dùng
        if(contact.isUser()){ //Kiểm tra xem người dùng có trong hệ thống hay không để bật chức năng nhắn tin
            holder.btnStartChatPhoneBook.setVisibility(View.VISIBLE); //Chỉnh button visible
            holder.btnStartChatPhoneBook.setOnClickListener(new View.OnClickListener() { //Gắn sự kiện gotochat
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
