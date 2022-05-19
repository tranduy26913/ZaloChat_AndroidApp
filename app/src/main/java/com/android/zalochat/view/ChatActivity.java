package com.android.zalochat.view;

import static android.view.View.GONE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.Util;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ImageButton btnBack, btnCall, btnVideoCall, btnChatSetting, btnEmoji, btnMoreHoz, btnRecSound, btnGetImage, btnSend;
    TextView txtUserName, txtOnlineStatus;
    EditText txtBodyMessage;
    LinearLayout chat_bar;
    ConstraintLayout layoutImgSendMessage;
    ImageView imgSendMessage;
    String chatId;
    User userOwn;
    UserChat friendUser;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerViewMessageChat;
    Bitmap myAvatar, friendAvatar;
    List<MessageChat> messageList = new ArrayList<MessageChat>();
    MessageAdapter messageAdapter = new MessageAdapter(messageList,ChatActivity.this);
    private SharedPreferences pref;
    private Uri fileImageSend;
    private UploadTask uploadTask;
    int SELECT_PICTURE = 200;
    final Gson gson =new Gson();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
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

        friendUser = (UserChat) getIntent().getSerializableExtra(Constants.USER_JSON);
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
            db.collection(Constants.CHAT_COLLECTION)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            Chat chat;
                            for (QueryDocumentSnapshot doc:value){
                                chat = doc.toObject(Chat.class);
                                if (chat.getSender().equals(userOwn.getUserId()) && chat.getReceiver().equals(friendUser.getUserId())) {
                                    chatId = chat.getId();

                                } else if (chat.getSender().equals(friendUser.getUserId()) && chat.getReceiver().equals(userOwn.getUserId())) {
                                    chatId = chat.getId();
                                }
                                if(!chatId.equals(""))
                                    LoadMessage();//tạm thời để ở đây
                            }
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
        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
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
        layoutImgSendMessage = findViewById(R.id.layoutImgSendMessage);
        imgSendMessage = findViewById(R.id.imgSendMessage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessageChat.setLayoutManager(layoutManager);
    }

    public void onClickSendMessage() {
        Chat chat;
        if (chatId.equals("")) {
            chatId = userOwn.getUserId().concat(friendUser.getUserId());
            chat = new Chat(chatId, userOwn.getUserId(), friendUser.getUserId(), (new Date()).getTime(), "Tin nhắn đầu tiên");
            db.collection(Constants.CHAT_COLLECTION)
                    .document(chatId)
                    .set(chat);
            LoadMessage();
        }
        UUID uuid =  UUID.randomUUID();
        if(layoutImgSendMessage.getVisibility()==View.VISIBLE){//upload ảnh
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("IMAGES");
            StorageReference filePath = storageRef.child(uuid.toString());
            uploadTask = filePath.putFile(fileImageSend);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        String path =  task.getResult().toString();
                        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), path,
                                new Date().getTime(),"LIKE",Constants.IMAGE);
                        db.collection(Constants.MESSAGE_COLLECTION)
                                .document(chatId)
                                .collection(Constants.SUBMESSAGE_COLLECTION)
                                .document(message.getId()).set(message);
                        db.collection(Constants.CHAT_COLLECTION)
                                .document(chatId)
                                .update("lastmessage","[Hình ảnh]");
                    }
                }
            });
            txtBodyMessage.setText("");
            imgSendMessage = null;
            fileImageSend = null;
            layoutImgSendMessage.setVisibility(GONE);
        }
        else {
            Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), txtBodyMessage.getText().toString(),
                    new Date().getTime(),"LIKE",Constants.TEXT);
            db.collection(Constants.MESSAGE_COLLECTION)
                    .document(chatId)
                    .collection(Constants.SUBMESSAGE_COLLECTION)
                    .document(message.getId()).set(message);
            db.collection(Constants.CHAT_COLLECTION)
                    .document(chatId)
                    .update("lastmessage",message.getContent());
            txtBodyMessage.setText("");
        }


    }

    private void LoadMessage(){
        db.collection(Constants.MESSAGE_COLLECTION)
                .document(chatId)
                .collection(Constants.SUBMESSAGE_COLLECTION)
                .orderBy("time")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageList.clear();
                        for (QueryDocumentSnapshot doc:value){
                            Message message = doc.toObject(Message.class);
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
                        recyclerViewMessageChat.setHasFixedSize(true);
                        recyclerViewMessageChat.setAdapter(messageAdapter);
                        recyclerViewMessageChat.setItemViewCacheSize(50);
                        recyclerViewMessageChat.setDrawingCacheEnabled(true);
                        recyclerViewMessageChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewMessageChat.smoothScrollToPosition(messageList.size());
                    }
                });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        mStartForResult.launch(Intent.createChooser(i, "Select Picture"));
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                            fileImageSend = intent.getData();
                            if (null != fileImageSend) {
                                InputStream imageStream = null;
                                try {
                                    imageStream = getContentResolver().openInputStream(fileImageSend);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                imgSendMessage.setImageBitmap(selectedImage);
                                layoutImgSendMessage.setVisibility(View.VISIBLE);

                                btnSend.getLayoutParams().width = 60;
                                btnMoreHoz.setVisibility(GONE);
                                btnRecSound.setVisibility(GONE);
                                btnGetImage.setVisibility(GONE);
                                btnSend.setVisibility(View.VISIBLE);
                            }

                    }
                }
            });

    public void onClickCancelSendImage(View view) {
        imgSendMessage.setImageBitmap(null);
        layoutImgSendMessage.setVisibility(GONE);
    }
}