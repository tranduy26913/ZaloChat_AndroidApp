package com.android.zalochat.view;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.adapter.MessageAdapter;
import com.android.zalochat.mapping.MessageMapping;
import com.android.zalochat.model.Chat;
import com.android.zalochat.model.Message;
import com.android.zalochat.model.Upload;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.MessageChat;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    //Li??n k???t ?????n c??c ph???n t??? ImageButton Back, Call, VideoCall, ChatSetting, Emoji, MoreHoz, RecSound, GetImage, Send tr??n layout
    ImageButton btnBack, btnCall, btnVideoCall, btnChatSetting, btnEmoji, btnMoreHoz, btnRecSound, btnGetImage, btnSend, btn_hold_to_rec_sound, btn_cancel_rec, btn_hand_rec;
    TextView txtUserName, txtOnlineStatus, txt_instruction_rec;//Li??n k???t ?????n c??c ph???n t??? TextView Username v?? Online Status
    EditText txtBodyMessage;//Li??n k???t ?????n ph???n t??? EditText BodyMessage tr??n layout
    LinearLayout chat_bar, linear_rec_sound, round_button;//Li??n k???t ?????n ph???n t??? LinearLayout chat_bar tr??n layout
    ConstraintLayout layoutImgSendMessage;//Li??n k???t ?????n ph???n t??? ConstraintLayout ImgSendMessage tr??n layout
    ImageView imgSendMessage;//Li??n k???t ?????n ph???n t??? ImageView imgSendMessage tr??n layout
    String chatId;//Bi???n l??u chatId
    User userOwn;//Bi???n l??u th??ng tin t??i kho???n (user) c???a m??nh (ng?????i g???i)
    UserChat friendUser;//Bi???n l??u th??ng tin t??i kho???n (user) c???a ng?????i kh??c (ng?????i nh???n)
    final FirebaseDatabase database = FirebaseDatabase.getInstance();//Bi???n l??u k???t n???i t???i database Firestore
    RecyclerView recyclerViewMessageChat;//Bi???n l??u Li??n k???t ?????n ph???n t??r RecyclerView MessageChat tr??n layout
    Bitmap myAvatar, friendAvatar;//Bi???n l??u h??nh ???nh avatar c???a ng?????i g???i v?? ng?????i nh???n (friend)
    List<MessageChat> messageList = new ArrayList<MessageChat>();//Danh s??ch tin nh???n ???????c hi???n th??? l??n recyclerview
    MessageAdapter messageAdapter;//Adapter c???a Message, ch???u tr??ch nhi???m hi???n th??? danh s??ch tin nh???n
    private SharedPreferences pref;//Bi???n l??u SharedPreferences
    private Uri fileImageSend;//Bi???n l??u Uri c???a file ???nh trong m??y khi ch???n ???nh ????? g???i ??i
    private Uri fileAudioSend;//Bi???n l??u Uri c???a file audio trong m??y khi ch???n ???nh ????? g???i ??i
    private UploadTask uploadTask;//Bi???n l??u t??c v??? Upload ???nh l??n database Firebase
    final Gson gson = new Gson();//Khai b??o m???t Gson ch???u tr??ch nhi???m x??? l?? li??n quan ?????n d??? li???u json
    private FirebaseFirestore db = FirebaseFirestore.getInstance();//L???y instance c???a Database Firestore
    private MediaRecorder mRecorder;//?????i t?????ng d??ng ????? ghi ??m
    // ???????ng d???n c???a file ??m thanh ??ang c?? trong m??y sau khi ghi ??m
    private static String fileAudioPath = null;
    // h???ng s??? l??u c???p quy???n d??ng ??m thanh
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);//Thi???t l???p layout activity_chat
        addControl();//Th???c hi???n c??c li??n k???t ?????n control trong layout
        addEvent();//Th???c hi???n th??m c??c s??? ki???n cho control
        pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Khai b??o SharedPredferences
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//l???y json user t??? share pref
        userOwn = gson.fromJson(userOwnJson, User.class);//parse sang class User
        Picasso.get().load(userOwn.getAvatar()).into(new Target() {//Load ???nh avatar
            @Override//Sau khi ???nh ???????c load
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
        loadDataIntent();//Load d??? li???u ???????c truy???n th??ng qua intent

    }

    //Load d??? li???u ???????c truy???n th??ng qua intent
    private void loadDataIntent() {

        friendUser = (UserChat) getIntent().getSerializableExtra(Constants.USER_JSON);//L???y th??ng tin c???a ng?????i nh???n tin
        txtUserName.setText(friendUser.getFullname());//G???n fullname v?? txtUsername ????? hi???n th??? tr??n giao di???n
        Picasso.get().load(friendUser.getAvatar()).into(new Target() {//Load ???nh t??? url
            @Override//Khi load th??nh c??ng
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                friendAvatar = bitmap;//g???n avatar c???a ng?????i nh???n v??o friendAvatar ????? hi???n th??? l??n giao di???n
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        if (friendUser.getChatId().equals("")) {//N???u kh??ng c?? chatId (t???c l?? 2 ng?????i ch??a nh???n tin n??o)
            chatId = "";//G???n chu???i r???ng ????? tr??nh l???i null
            db.collection(Constants.CHAT_COLLECTION)//t??m ki???m tr??n Collection CHATS
                    //T??m ki???m xem c?? t???n t???i document Chat n??o m?? c?? danh s??ch users bao g???m id c???a userOwn (ng?????i g???i) v?? friendUser (ng?????i nh???n)
                    .whereArrayContains("users", userOwn.getUserId())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            Chat chat;
                            for (QueryDocumentSnapshot doc : value) {//Duy???t qua danh s??ch c??c document t??m ???????c
                                chat = doc.toObject(Chat.class);//??p ki???u sang object Chat
                                if(chat.getUsers().contains(friendUser.getUserId())){
                                    chatId = chat.getId();//G???n gi?? tr??? cho chatId
                                    LoadMessage();//Load ra danh s??ch tin nh???n
                                    break;
                                }

                            }
                        }
                    });
        } else {//Tr?????ng h???p c?? chatId
            chatId = friendUser.getChatId();
            LoadMessage();//Load ra danh s??ch tin nh???n
        }

    }


    //Th??m c??c s??? ki???n cho c??c control c???n thi???t
    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {
        //X??? l?? s??? ki???n khi nh???p tin nh???n v??o khung chat
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
        btnRecSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecordSoundWidget();
            }
        });//n??t ????? m??? m??n ch???c n??ng ghi ??m trong ??o???n chat
        btn_hold_to_rec_sound.setOnTouchListener(new View.OnTouchListener() {//n??t gi??? ????? ghi ??m
            @Override
            public boolean onTouch(View v, MotionEvent event) {//s??? ki???n ng?????i d??ng ch???m v??o m??n h??nh
                float tempX = event.getX();//l???y t???a ????? x m?? ng?????i d??ng ch???m v??o m??n h??nh.
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//n???u ng?????i d??ng ??ang ch???m v??o m??n h??nh
                    startRecording();//n???u ng?????i d??ng gi??? m??n h??nh th?? thu ??m thanh
                } else if (event.getAction() == MotionEvent.ACTION_UP) {//n???u ng?????i d??ng r???i tay kh???i m??n h??nh
                    pauseRecording();//d???ng ghi ??m khi ng?????i d??ng kh??ng c??n nh???n gi???
                    if (tempX <= -120) {//n???u n??i ng?????i d??ng th??? tay ra c??ch ??i???m ch???m ban ?????u l?? 120 v??? ph??a tr??i m??n h??nh
                        //x??a ghi ??m kh??ng g???i ??i
                        fileAudioPath = null;
                        btn_cancel_rec.setVisibility(GONE);//???n n??t x??a
                        btn_hand_rec.setVisibility(GONE);//???n n??t ghi ??m r??nh tay
                    } else {
                        onClickSendMessage();//n???u ng?????i d??ng kh??ng th??? v??o v??ng x??a th?? file ghi ??m s??? ???????c g???i ??i
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {//n???u ng?????i d??ng ??ang ch???m v?? di chuy???n trong m??n h??nh
                    if (tempX <= -120) {//n???u ng?????i d??ng gi??? tay tr??n m??n h??nh c??ch ??i???m ch???m ?????u ti??n 120
                        btn_cancel_rec.setVisibility(View.VISIBLE);//ko ???n n??t x??a ??m thanh
                        btn_hand_rec.setVisibility(View.VISIBLE);//kh??ng ???n n??t ghi ??m r??nh tay
                    } else {
                        btn_cancel_rec.setVisibility(GONE);//n???u kh??ng n???m trong  v??ng x??a ghi ??m th?? s??? ???n n??t kh??ng c???n thi???t
                        btn_hand_rec.setVisibility(GONE);//n???u kh??ng n???m trong  v??ng x??a ghi ??m th?? s??? ???n n??t kh??ng c???n thi???t
                    }
                }
                return true;
            }
        });
    }

    private void pauseRecording() {
        txt_instruction_rec.setText("Nh???n gi??? ????? ghi ??m");//hi???n th??? n??t ghi ??m b??o l?? nh???n gi??? n??t thu ????? ghi ??m
        mRecorder.stop();//d???ng ghi ??m
        mRecorder.release();//release ??m thanh ????? xu???t file
        mRecorder = null;//g??n r???ng mRecoder
    }

    private void startRecording() {
        txt_instruction_rec.setText("Th??? ra ????? g???i, di chuy???n sang tr??i ????? x??a");//set Text th??? ra g???i file ??m thanh
        fileAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();//l???y ???????ng d???n file ??m thanh
        fileAudioPath += "/AudioRecording.3gp";//file t??n l?? AudioRecording v???i ?????nh d???ng l?? 3gp

        mRecorder = new MediaRecorder();//t???o m???i bi???n mRecoder

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//l???y ngu???n thu ??m t??? mic

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//ch???nh file audio ?????nh d???ng l?? 3gp

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//encode theo AMR_NB

        mRecorder.setOutputFile(fileAudioPath);//thi???t l???p ???????ng d???n file audio khi xu???t ra
        try {
            mRecorder.prepare();//chu???n b??? cho quy tr??nh ghi ??m
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
        mRecorder.start();//b???t ?????u ghi ??m
    }

    private void showRecordSoundWidget() {
        if (checkPermissions()) {//check c?? c???p quy???n l??u tr??? ch??a
            if (linear_rec_sound.getVisibility() == View.VISIBLE) {//n???u widget ghi ??m ???? hi???n th??? th?? ???n widget ???? ??i,
                // gi???m chi???u d??i b???ng 0
                linear_rec_sound.setVisibility(GONE);
                linear_rec_sound.getLayoutParams().height = 0;
                round_button.getLayoutParams().height = 0;
                btn_hold_to_rec_sound.getLayoutParams().height = 0;
            } else if (linear_rec_sound.getVisibility() == GONE) {//n???u widget ghi ??m ???? ???n th?? hi???n th??? widget ???? ??i,
                // t??ng chi???u d??i, cao v???a m???c ????? nh??n th???y widget ????
                linear_rec_sound.setVisibility(View.VISIBLE);
                linear_rec_sound.getLayoutParams().height = 500;
                round_button.getLayoutParams().height = 200;
                btn_hold_to_rec_sound.getLayoutParams().height = 200;
            }
        } else {
            requestPermissions();//y??u c???u c???p quy???n n???u check quy???n ch??a ???????c c???p
        }

    }

    private void addControl() {
        btnBack = findViewById(R.id.btn_back);//G???n ph???n t??? btn_back tr??n layout cho bi???n
        btnCall = findViewById(R.id.btn_call);//G???n ph???n t??? btn_call tr??n layout cho bi???n
        btnVideoCall = findViewById(R.id.btn_video_call);//G???n ph???n t??? btn_video_call tr??n layout cho bi???n
        btnChatSetting = findViewById(R.id.btn_chat_setting);//G???n ph???n t??? chat_setting tr??n layout cho bi???n
        btnEmoji = findViewById(R.id.btn_emoji_bottom_sheet);//G???n ph???n t??? btn_emoji_bottom_sheet tr??n layout cho bi???n
        btnMoreHoz = findViewById(R.id.btn_more_horizontal);//G???n ph???n t??? btn_more_horizontal tr??n layout cho bi???n
        btnRecSound = findViewById(R.id.btn_rec_sound);//G???n ph???n t??? btn_rec_sound tr??n layout cho bi???n
        btnGetImage = findViewById(R.id.btn_get_image);//G???n ph???n t??? btn_get_image tr??n layout cho bi???n
        btnSend = findViewById(R.id.btn_send);//G???n ph???n t??? btn_send tr??n layout cho bi???n
        txtUserName = findViewById(R.id.txt_user_name);//G???n ph???n t??? txt_user_name tr??n layout cho bi???n
        txtOnlineStatus = findViewById(R.id.txt_online_status);//G???n ph???n t??? txt_online_status tr??n layout cho bi???n
        txtBodyMessage = findViewById(R.id.txt_body_message);//G???n ph???n t??? txt_body_message tr??n layout cho bi???n
        chat_bar = findViewById(R.id.chat_bar);//G???n ph???n t??? chat_bar tr??n layout cho bi???n
        recyclerViewMessageChat = findViewById(R.id.recyclerViewMessageChat);//G???n ph???n t??? recyclerViewMessageChat tr??n layout cho bi???n
        layoutImgSendMessage = findViewById(R.id.layoutImgSendMessage);//G???n ph???n t??? layoutIngSendMessage tr??n layout cho bi???n
        imgSendMessage = findViewById(R.id.imgSendMessage);//G???n ph???n t??? imgSendMessage tr??n layout cho bi???n
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//Khai b??o m???t LinearLayoutManager ????? thi???t l???p layout cho RecyclerView
        layoutManager.setStackFromEnd(true);//Th??? t??? load ra danh s??ch tin nh???n ??i t??? d?????i l??n
        recyclerViewMessageChat.setLayoutManager(layoutManager);//G???n layout manager cho recyclerViewMessageChat
        linear_rec_sound = findViewById(R.id.linear_rec_sound);//g???n ph???n t??? v??ng ch???c n??ng ghi ??m trong layout cho bi???n
        round_button = findViewById(R.id.round_button);//g???n ph???n t??? n??t d???ng h??nh tr??n v??o bi???n layout
        btn_hold_to_rec_sound = findViewById(R.id.btn_hold_to_rec_sound);//g???n ph???n t??? n??t gi??? ghi ??m cho bi???n layout
        txt_instruction_rec = findViewById(R.id.txt_instruction_rec);//g???n text view h?????ng d???n c??ch ghi ??m
        btn_cancel_rec = findViewById(R.id.btn_cancel_rec);//g???n bi???n n??t h???y b???n ghi ??m v??o bi???n layout
        btn_hand_rec = findViewById(R.id.btn_hand_rec);//g???n bi???n ghi ??m r??nh tay v??o bi???n layout
    }

    //X??? l?? s??? ki???n khi b???m n??t Send
    public void onClickSendMessage() {
        Chat chat;//Khai b??o object Chat
        if (chatId.equals("")) {//N???u chatId r???ng t???c 2 ng?????i ch??a c?? nh???n chat
            DocumentReference chatRef = db.collection(Constants.CHAT_COLLECTION).document();//T???o m???t DocumentReference li??n k???t t???i database
            //m???c ?????nh khi t???o Document theo c??ch tr??n s??? t???o id t??? ?????ng
            chatId = chatRef.getId();//l???y id sau khi document ???????c t???o
            //Kh???i t???o object Chat v???i ?????y ????? th??ng tin id, users g???m 2 ng?????i, th???i gian v?? tin nh???n cu???i c??ng
            chat = new Chat(chatId, Arrays.asList(userOwn.getUserId(), friendUser.getUserId()), (new Date()).getTime(), "Tin nh???n ?????u ti??n");
            chatRef.set(chat);//set d??? li???u c???a chat cho document
            LoadMessage();//g???i h??m load tin nh???n
        }
        UUID uuid = UUID.randomUUID();//T???o 1 uuid ng???u nhi??n,d??ng uuid v?? id n??y ?????m b???o kh??ng tr??ng l???p
        if (layoutImgSendMessage.getVisibility() == View.VISIBLE) {//upload ???nh
            FirebaseStorage storage = FirebaseStorage.getInstance();//Khai b??o storage li??n k???t t???i Storage tr??n Firebase
            StorageReference storageRef = storage.getReference().child("IMAGES");//Khai b??o StorageReference ch??? ?????n nh??nh IMAGES
            StorageReference filePath = storageRef.child(uuid.toString());// Khai b??o 1 ???????ng ?????n tr??n StorageReference
            uploadTask = filePath.putFile(fileImageSend);//?????y file l??n database
            uploadTask.continueWithTask(new Continuation() {//Theo d??i ti???n tr??nh upload file
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {//N???u upload l???i
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();//Tr??? v??? ???????ng d???n c???a file sau khi upload th??nh c??ng
                }
            }).addOnCompleteListener(new OnCompleteListener() {//X??? l?? sau khi upload xong
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        String path = task.getResult().toString();//L???y ???????ng d???n t???i file v???a upload xong
                        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), path,
                                new Date().getTime(), -1, Constants.IMAGE);//T???o 1 object Message ch???a n???i dung tin nh???n
                        db.collection(Constants.MESSAGE_COLLECTION)//L???y collection MESSAGES trong database
                                .document(chatId)//L???y ?????n document c?? id l?? chatId
                                .collection(Constants.SUBMESSAGE_COLLECTION)//L???y m???t sub collection SUBMESSAGE ch???a danh s??ch tin nh???n
                                .document(message.getId()).set(message);//G???n th??ng tin c???a object message v??o Database

                        db.collection(Constants.CHAT_COLLECTION)
                                .document(chatId)
                                .update("lastmessage", "[H??nh ???nh]");//C???p nh???t l???i th??ng tin c???a document Chat tr??n database
                        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//T???o m???t DocumentReference li??n k???t t???i database
                        DocumentReference subuploadRef = uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document();//T???o m???t DocumentReference li??n k???t t???i database
                        Upload newUpload = new Upload();
                        newUpload.setUrl("IMAGES/"+uuid.toString());
                        newUpload.setUserId(userOwn.getUserId());
                        newUpload.setDate(new Date().getTime());
                        subuploadRef.set(newUpload);
                    }
                }
            });
            txtBodyMessage.setText("");//G???n l???i chu???i r???ng cho khung chat sau khi tin ???????c g???i ??i
            imgSendMessage = null;//G???n l???i gi?? tr??? null cho h??nh ???nh sau khi g???i ??i
            fileImageSend = null;//G???n l???i gi?? tr??? null cho file ???nh ???????c ch???n sau khi g???i ??i
            layoutImgSendMessage.setVisibility(GONE);//???n ??i layout ch???a ???nh ???????c ch???n
        } else if (fileAudioPath != null) {//ki???m tra ???????ng d???n c?? tr???ng kh??ng n???u ko tr???ng th?? ???? l?? 1 tin nh???n ??m thanh v?? s??? ???????c g???i ??i ngay
            FirebaseStorage storage = FirebaseStorage.getInstance();//Khai b??o storage li??n k???t t???i Storage tr??n Firebase
            StorageReference storageRef = storage.getReference().child("audios");//Khai b??o StorageReference ch??? ?????n nh??nh IMAGES
            StorageReference filePath = storageRef.child(uuid.toString());// Khai b??o 1 ???????ng ?????n tr??n StorageReference
            fileAudioSend=Uri.fromFile(new File(fileAudioPath));//l???y uri t??? file audio ???? ???????c l??u
            uploadTask = filePath.putFile(fileAudioSend);//?????y file l??n database
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {//N???u upload l???i
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();//Tr??? v??? ???????ng d???n c???a file sau khi upload th??nh c??ng
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        String path = task.getResult().toString();//L???y ???????ng d???n t???i file v???a upload xong
                        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), path,
                                new Date().getTime(), -1, Constants.SOUND);//T???o 1 object Message ch???a n???i dung tin nh???n d???ng ??m thanh
                        db.collection(Constants.MESSAGE_COLLECTION)//L???y collection MESSAGES trong database
                                .document(chatId)//L???y ?????n document c?? id l?? chatId
                                .collection(Constants.SUBMESSAGE_COLLECTION)//L???y m???t sub collection SUBMESSAGE ch???a danh s??ch tin nh???n
                                .document(message.getId()).set(message);//G???n th??ng tin c???a object message v??o Database
                        db.collection(Constants.CHAT_COLLECTION)
                                .document(chatId)
                                .update("lastmessage", "[??m thanh]");//C???p nh???t l???i th??ng tin c???a document Chat tr??n database
//                        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//T???o m???t DocumentReference li??n k???t t???i database
//                        DocumentReference subuploadRef = uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document();//T???o m???t DocumentReference li??n k???t t???i database
//                        Upload newUpload = new Upload();
//                        newUpload.setUrl("audios/"+uuid.toString());
//                        newUpload.setUserId(userOwn.getUserId());
//                        newUpload.setDate(new Date().getTime());
//                        subuploadRef.set(newUpload);
                    }
                }
            });
            fileAudioPath = null;//x??a path
            fileAudioSend = null;//x??a file ??m thanh ???????c l??u trong bi???n file audio sau khi ???????c g???i
        } else {
            //Kh???i t???o m???t message ch???a n???i dung tin nh???n
            Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), txtBodyMessage.getText().toString(),
                    new Date().getTime(), -1, Constants.TEXT);
            //truy c???p ?????n Collection MESSAGES, document chatId, subcollection SUBMESSAGE
            db.collection(Constants.MESSAGE_COLLECTION)
                    .document(chatId)
                    .collection(Constants.SUBMESSAGE_COLLECTION)
                    .document(message.getId()).set(message);//????a th??ng tin c???a message th??nh 1 document tr??n Database
            db.collection(Constants.CHAT_COLLECTION)
                    .document(chatId)
                    .update("lastmessage", message.getContent());//C???p nh???p l???i field lassmessage c???a Chat
            txtBodyMessage.setText("");//G???n l???i chu???i r???ng cho khung nh???p tin nh???n sau khi g???i ??i
        }


    }

    //H??m x??? l?? load tin nh???n
    private void LoadMessage() {
        if (chatId == null || chatId.equals(""))//N???u chatId null ho???c l?? chu???i r???ng th?? d???ng x??? l??
            return;
        messageAdapter = new MessageAdapter(messageList, ChatActivity.this, chatId);//Kh???i t???o 1 Message Adapter ?????m nhi???m hi???n th??? danh s??ch tin nh???n
        //Th???c hi???n truy v???n ?????n collection MESSAGES
        db.collection(Constants.MESSAGE_COLLECTION)
                .document(chatId)
                .collection(Constants.SUBMESSAGE_COLLECTION)
                .orderBy("time")//S???p x???p c??c tin nh???n theo th??? t??? th???i gian
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override//X??? l?? khi truy v???n xong
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageList.clear();//D???n s???ch danh s??ch tin nh???n c??
                        for (QueryDocumentSnapshot doc : value) {//Duy???t qua t???ng tin nh???n
                            Message message = doc.toObject(Message.class);//Mapping t??? document sang object Message
                            MessageChat messageChat;//Khai b??o 1 object MessageChat, c?? t??c d???ng ch???a th??ng tin ????? hi???n th??? l??n cho ng?????i d??ng
                            if (message.getSender().equals(userOwn.getUserId()))//n???u l?? ng?????i g???i l?? m??nh
                            {
                                //G???n Avatar l?? c???a m??nh
                                messageChat = MessageMapping.EntityToMessageChat(message, myAvatar);
                            } else {
                                //G???n Avatar l?? c???a ng?????i nh???n
                                messageChat = MessageMapping.EntityToMessageChat(message, friendAvatar);
                            }
                            messageList.add(messageChat);//Th??m tin nh???n v??o danh s??ch tin nh???n
                        }
                        recyclerViewMessageChat.setHasFixedSize(true);//thi???t l???p cho ph??p t??? t???i ??u k??ch th?????c hi???n th???
                        recyclerViewMessageChat.setAdapter(messageAdapter);//set adapter
                        //thi???t l???p b??? nh??? cache cho recycler view
                        recyclerViewMessageChat.setItemViewCacheSize(50);
                        recyclerViewMessageChat.setDrawingCacheEnabled(true);
                        recyclerViewMessageChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        messageAdapter.notifyDataSetChanged();//Th??ng b??o ?????n adapter l?? d??? li???u c?? thay ?????i ????? Adapter hi???n th??? l???i danh s??ch m???i
                        recyclerViewMessageChat.smoothScrollToPosition(messageList.size());//Thi???t l???p ????? m?????t khi cu???n
                    }
                });
    }

    //X??? l?? ch???n h??nh ???nh t??? thi???t b???
    void imageChooser() {
        Intent i = new Intent();//Kh???i t???o 1 intent
        i.setType("image/*");//thi???t l???p lo???i d??? li???u s??? ch???n
        i.setAction(Intent.ACTION_GET_CONTENT);//Thi???t l???p lo???i h??nh ?????ng l?? ch???n
        mStartForResult.launch(Intent.createChooser(i, "Select Picture"));//Kh???i ch???y Intent v???i ti??u ????? Select Picture
    }

    //Khai b??o 1 RegisterForActivityResult ????? x??? l?? khi ch???n ???nh xong
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();//L???y d??? li???u ???????c tr??? v??? t??? intent sau khi ch???n file xong
                        fileImageSend = intent.getData();//L???y Uri c???a ???nh
                        if (null != fileImageSend) {//ki???m tra n???u kh??c null
                            InputStream imageStream = null;//Khai b??o imageStream ????? ch???a d??? li???u ???nh
                            try {
                                imageStream = getContentResolver().openInputStream(fileImageSend);//load ???nh t??? uri
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);//Decode t??? stream sang d??? li???u ???nh d???ng bitmap
                            imgSendMessage.setImageBitmap(selectedImage);//set d??? li???u ???nh cho imgSendMessage
                            layoutImgSendMessage.setVisibility(View.VISIBLE);//Hi???n l??n layout ch???a ???nh ???????c ch???n

                            btnSend.getLayoutParams().width = 60;//??i???u ch???nh k??ch th?????c n??t g???i
                            btnMoreHoz.setVisibility(GONE);//???n btn MoreHoz
                            btnRecSound.setVisibility(GONE);//???n btn RecSound
                            btnGetImage.setVisibility(GONE);//???n btn GetImage
                            btnSend.setVisibility(View.VISIBLE);//???n btn Send
                        }

                    }
                }
            });

    //X??? l?? s??? ki???n hu??? g???i ???nh
    public void onClickCancelSendImage(View view) {
        imgSendMessage.setImageBitmap(null);//G???n gi?? tr??? null cho imgSend
        layoutImgSendMessage.setVisibility(GONE);
    }

    public boolean checkPermissions() {
        //y??u c???u c???p 3 quy???n l??u tr??? b??? nh??? ????? l??u file ??m thanh
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        //y??u c???u c???p quy???n s??? hi???n th??? c???a s??? h??? th???ng
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}