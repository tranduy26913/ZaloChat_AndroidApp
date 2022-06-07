package com.android.zalochat.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.adapter.UserChatAdapter;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.mapping.UserMapping;
import com.android.zalochat.model.Chat;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerViewUserChat;//Liên kết với RecyclerView số điện thoại
    private User userOwn;//Biến lưu thông tin tài khoản của mình
    private FirebaseFirestore database;//BIến lưu liên kết với database
    private UserChatAdapter userChatAdapter;//Userchat Adapter
    private List<UserChat> userChatList = new ArrayList<>();//Danh sách các tài khoản chat
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseFirestore.getInstance();//Gắn instance vào biến database
        recyclerViewUserChat = view.findViewById(R.id.recyclerViewUserChat);//Liên kết layout Recycler view Userchat với biến
        SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Khai báo Shared Preferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy thông tin tài khoản của mình từ Shared Preferences
        try {
            Gson gson  = new Gson();//Khai báo 1 Gson để làm việc với Json
            userOwn = gson.fromJson(jsonUser,User.class);//Chuyển thông tin từ json sang object User để sử dụng
        }catch (Exception ex) {
            //GotoLogin();
        }
        LoadUserChat();//Load ra danh sách User Chat
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    private void LoadUserChat(){
        //Collection CHATS có nhiệm vụ đánh dấu 2 tài khoản có nhắn tin với nhau
        //Chỉ khi 2 tài khoản có nhắn tin với nhau mới tạo thành 1 document Chat

        // truy vấn vào nhánh id của Collection Chat để tìm ra danh sách những người có nhắn tin với mình
        CollectionReference chatRef = database.collection(Constants.CHAT_COLLECTION);
        chatRef.whereArrayContains("users",userOwn.getUserId())
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userChatList.clear();//Xoá danh sách Userchat cũ
                for(QueryDocumentSnapshot doc:value){//Duyệt qua từng document thoả điều kiện ở trên
                    Chat chat = doc.toObject(Chat.class);//Mapping từ Document sang object Chat
                    String idFriend ="";//lưu id của tài khoản khác

                        for (String id:chat.getUsers()) {//duyệt qua danh sách user id trong object Chat
                            if(!id.equals(userOwn.getUserId())){//Nếu không phải tài khoản của mình
                                idFriend = id;//Gắn id cho idFriend
                            }
                        }

                    Task<DocumentSnapshot> user = database.collection(Constants.USER_COLLECTION)
                            .document(idFriend)//Tìm kiếm thông tin user có id = idFriend trong database
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().exists()){
                                            //Mapping từ User sang UserChat rồi add vào danh sách Userchat
                                            userChatList.add(UserMapping.EntityToUserchat(task.getResult().toObject(User.class)
                                                    ,chat.getLastmessage(),chat.getId()));
                                            //gọi hàm notify để thông báo cho adapter biết dữ liệu có sự thay đổi
                                            userChatAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                }

            }
        });
//Khởi tạo môt User Chat Adapter
        userChatAdapter = new UserChatAdapter(userChatList, new IClickItemUserChatListener() {
            //Hàm có nhiệm vụ định nghĩa sự kiện khi click vào item trong user chat adapter
            @Override
            public void onClickItemUserChat(UserChat userChat) {
                GoToChatActivity(userChat);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());//Khai báo một LinearLayoutManager
        recyclerViewUserChat.setAdapter(userChatAdapter);//Set adapter cho recycler View
        recyclerViewUserChat.setLayoutManager(linearLayoutManager);//Set linearlayout cho recycler view nhằm định nghĩa layout khi hiển thị
        recyclerViewUserChat.setHasFixedSize(true);//Set kích thước có thể chỉnh được
    }

    private void GoToChatActivity(UserChat userChat) {//Mở Chat activity
        Intent intent = new Intent(this.getContext(), ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,userChat);//Truyền thông tin UserChat qua Chat Activity
        startActivity(intent);
    }
}