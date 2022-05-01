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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.adapter.UserChatAdapter;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.mapping.UserMapping;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.ChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerViewUserChat;
    private User userOwn;//Tài khoản của mình
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
        recyclerViewUserChat = view.findViewById(R.id.recyclerViewUserChat);
        SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);
        String jsonUser = ref.getString(Constants.USER_JSON, "");
        try {
            Gson gson  = new Gson();
            userOwn = gson.fromJson(jsonUser,User.class);
        }catch (Exception ex) {
            //GotoLogin();
        }
        LoadUserChat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    private void LoadUserChat(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // truy vấn vào nhánh username mà người dùng nhập
        DatabaseReference users = firebaseDatabase.getReference("USERS");

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, User>> objectsGTypeInd =
                        new GenericTypeIndicator<HashMap<String, User>>() {
                        };
                Map<String, User> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                final List<User> objectArrayList = new ArrayList<>(objectHashMap.values());
                final List<UserChat> userChatList = new ArrayList<>();
                for (User user: objectArrayList ) {
                    if(!user.getUserId().equals(userOwn.getUserId()))
                        userChatList.add(UserMapping.EntityToUserchat(user,"Hãy bắt đầu gửi tin nhắn đầu tiên"));
                }
                UserChatAdapter userChatAdapter = new UserChatAdapter(userChatList, new IClickItemUserChatListener() {
                    @Override
                    public void onClickItemUserChat(UserChat userChat) {
                        GoToChatActivity(userChat);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerViewUserChat.setAdapter(userChatAdapter);
                recyclerViewUserChat.setLayoutManager(linearLayoutManager);
                recyclerViewUserChat.setHasFixedSize(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void GoToChatActivity(UserChat userChat) {
        Intent intent = new Intent(this.getContext(), ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,userChat);
        startActivity(intent);
    }
}