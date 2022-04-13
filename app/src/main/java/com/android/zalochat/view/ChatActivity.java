package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonWriter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.adapter.MessageAdapter;
import com.android.zalochat.mapping.MessageMapping;
import com.android.zalochat.model.Chat;
import com.android.zalochat.model.Message;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.MessageChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.Util;
import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ImageButton btnBack, btnCall, btnVideoCall, btnChatSetting, btnEmoji, btnMoreHoz, btnRecSound, btnGetImage, btnSend;
    TextView txtUserName, txtOnlineStatus;
    EditText txtBodyMessage;
    LinearLayout chat_bar;
    String chatId;
    User userOwn, friendUser;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerViewMessageChat;
    Bitmap myAvatar, friendAvatar;
    List<MessageChat> messageList;
    private SharedPreferences pref;
    final Gson gson =new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addControl();
        addEvent();
        pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//lấy json user từ share pref
        userOwn = gson.fromJson(userOwnJson,User.class);//parse sang class User
        Picasso.get().load(userOwn.getAvatar()).into(new Target() {//Load ảnh avatar
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                myAvatar = bitmap;
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        loadDataIntent();

    }

    private void loadDataIntent() {
        String userJson = getIntent().getStringExtra(Constants.USER_JSON);
        friendUser = gson.fromJson(userJson,User.class);
        txtUserName.setText(friendUser.getFullname());
        Picasso.get().load(friendUser.getAvatar()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                friendAvatar = bitmap;
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        if (getIntent().getStringExtra("chatId") == null) {
            chatId = "";
            DatabaseReference chats = database.getReference("CHATS");//lấy data
            chats.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getSender().equals(userOwn.getUserId()) && chat.getReceiver().equals(friendUser.getUserId())) {
                            chatId = chat.getId();

                        } else if (chat.getSender().equals(friendUser.getUserId()) && chat.getReceiver().equals(userOwn.getUserId())) {
                            chatId = chat.getId();
                        }
                    }
                    if(!chatId.equals(""))
                        LoadMessage();//tạm thời để ở đây
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else
            chatId = getIntent().getStringExtra("chatId");

    }


    private void addEvent() {
        txtBodyMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnMoreHoz.getLayoutParams().width = 0;
                    btnRecSound.getLayoutParams().width = 0;
                    btnGetImage.getLayoutParams().width = 0;
                    btnSend.getLayoutParams().width = 60;
                    btnMoreHoz.setVisibility(View.INVISIBLE);
                    btnRecSound.setVisibility(View.INVISIBLE);
                    btnGetImage.setVisibility(View.INVISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                } else if (charSequence.length() == 0) {
                    btnMoreHoz.setVisibility(View.VISIBLE);
                    btnRecSound.setVisibility(View.VISIBLE);
                    btnGetImage.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.INVISIBLE);
                    btnMoreHoz.getLayoutParams().width = 50;
                    btnRecSound.getLayoutParams().width = 50;
                    btnGetImage.getLayoutParams().width = 50;
                    btnSend.getLayoutParams().width = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnBack.setOnClickListener(view -> {
            finish();
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSendMessage();
            }
        });

    }

    private void addControl() {
        btnBack = findViewById(R.id.btn_back);
        btnCall = findViewById(R.id.btn_call);
        btnVideoCall = findViewById(R.id.btn_video_call);
        btnChatSetting = findViewById(R.id.btn_chat_setting);
        btnEmoji = findViewById(R.id.btn_emoji_bottom_sheet);
        btnMoreHoz = findViewById(R.id.btn_more_horizontal);
        btnRecSound = findViewById(R.id.btn_rec_sound);
        btnGetImage = findViewById(R.id.btn_get_image);
        btnSend = findViewById(R.id.btn_send);
        txtUserName = findViewById(R.id.txt_user_name);
        txtOnlineStatus = findViewById(R.id.txt_online_status);
        txtBodyMessage = findViewById(R.id.txt_body_message);
        chat_bar = findViewById(R.id.chat_bar);
        recyclerViewMessageChat = findViewById(R.id.recyclerViewMessageChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessageChat.setLayoutManager(layoutManager);
    }

    public void onClickSendMessage() {
        Chat chat;
        if (chatId.equals("")) {
            chatId = userOwn.getUserId().concat(friendUser.getUserId());
            chat = new Chat(chatId, userOwn.getUserId(), friendUser.getUserId(), (new Date()).getTime(), "Tin nhắn đầu tiên");
            database.getReference("CHATS").child(chatId).setValue(chat);
            LoadMessage();
        }
        UUID uuid =  UUID.randomUUID();
        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), txtBodyMessage.getText().toString(),
                new Date().getTime(),"Like","text");

        DatabaseReference ref = database.getReference("MESSAGES");
        ref.child(chatId).child(String.valueOf(message.getTime())).setValue(message);
        txtBodyMessage.setText("");
        LoadMessage();

    }

    private void LoadMessage(){
        messageList = new ArrayList<>();
        DatabaseReference reference = database.getReference("MESSAGES").child(chatId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren() ) {
                    Message message = snapshot.getValue(Message.class);
                    MessageChat messageChat;
                    if(message.getSender().equals(userOwn.getUserId()))//nếu là người gửi là mình
                    {
                         messageChat= MessageMapping.EntityToMessageChat(message,myAvatar);
                    }
                    else {
                        messageChat = MessageMapping.EntityToMessageChat(message,friendAvatar);
                    }
                    messageList.add(messageChat);

                }
                MessageAdapter messageAdapter = new MessageAdapter(messageList,ChatActivity.this);
                recyclerViewMessageChat.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}