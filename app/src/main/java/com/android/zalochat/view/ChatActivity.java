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
    //Liên kết đến các phần tử ImageButton Back, Call, VideoCall, ChatSetting, Emoji, MoreHoz, RecSound, GetImage, Send trên layout
    ImageButton btnBack, btnCall, btnVideoCall, btnChatSetting, btnEmoji, btnMoreHoz, btnRecSound, btnGetImage, btnSend, btn_hold_to_rec_sound, btn_cancel_rec, btn_hand_rec;
    TextView txtUserName, txtOnlineStatus, txt_instruction_rec;//Liên kết đến các phần tử TextView Username và Online Status
    EditText txtBodyMessage;//Liên kết đến phần tử EditText BodyMessage trên layout
    LinearLayout chat_bar, linear_rec_sound, round_button;//Liên kết đến phần tử LinearLayout chat_bar trên layout
    ConstraintLayout layoutImgSendMessage;//Liên kết đến phần tử ConstraintLayout ImgSendMessage trên layout
    ImageView imgSendMessage;//Liên kết đến phần tử ImageView imgSendMessage trên layout
    String chatId;//Biến lưu chatId
    User userOwn;//Biến lưu thông tin tài khoản (user) của mình (người gửi)
    UserChat friendUser;//Biến lưu thông tin tài khoản (user) của người khác (người nhận)
    final FirebaseDatabase database = FirebaseDatabase.getInstance();//Biến lưu kết nối tới database Firestore
    RecyclerView recyclerViewMessageChat;//Biến lưu Liên kết đến phần tưr RecyclerView MessageChat trên layout
    Bitmap myAvatar, friendAvatar;//Biến lưu hình ảnh avatar của người gửi và người nhận (friend)
    List<MessageChat> messageList = new ArrayList<MessageChat>();//Danh sách tin nhắn được hiển thị lên recyclerview
    MessageAdapter messageAdapter;//Adapter của Message, chịu trách nhiệm hiển thị danh sách tin nhắn
    private SharedPreferences pref;//Biến lưu SharedPreferences
    private Uri fileImageSend;//Biến lưu Uri của file ảnh trong máy khi chọn ảnh để gửi đi
    private Uri fileAudioSend;//Biến lưu Uri của file audio trong máy khi chọn ảnh để gửi đi
    private UploadTask uploadTask;//Biến lưu tác vụ Upload ảnh lên database Firebase
    final Gson gson = new Gson();//Khai báo một Gson chịu trách nhiệm xử lý liên quan đến dữ liệu json
    private FirebaseFirestore db = FirebaseFirestore.getInstance();//Lấy instance của Database Firestore
    private MediaRecorder mRecorder;//đối tượng dùng để ghi âm
    // Đường dẫn của file âm thanh đang có trong máy sau khi ghi âm
    private static String fileAudioPath = null;
    // hằng số lưu cấp quyền dùng âm thanh
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);//Thiết lập layout activity_chat
        addControl();//Thực hiện các liên kết đến control trong layout
        addEvent();//Thực hiện thêm các sự kiện cho control
        pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Khai báo SharedPredferences
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//lấy json user từ share pref
        userOwn = gson.fromJson(userOwnJson, User.class);//parse sang class User
        Picasso.get().load(userOwn.getAvatar()).into(new Target() {//Load ảnh avatar
            @Override//Sau khi ảnh được load
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
        loadDataIntent();//Load dữ liệu được truyền thông qua intent

    }

    //Load dữ liệu được truyền thông qua intent
    private void loadDataIntent() {

        friendUser = (UserChat) getIntent().getSerializableExtra(Constants.USER_JSON);//Lấy thông tin của người nhận tin
        txtUserName.setText(friendUser.getFullname());//Gắn fullname và txtUsername để hiển thị trên giao diện
        Picasso.get().load(friendUser.getAvatar()).into(new Target() {//Load ảnh từ url
            @Override//Khi load thành công
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                friendAvatar = bitmap;//gắn avatar của người nhận vào friendAvatar để hiển thị lên giao diện
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        if (friendUser.getChatId().equals("")) {//Nếu không có chatId (tức là 2 người chưa nhắn tin nào)
            chatId = "";//Gắn chuỗi rỗng để tránh lỗi null
            db.collection(Constants.CHAT_COLLECTION)//tìm kiếm trên Collection CHATS
                    //Tìm kiếm xem có tồn tại document Chat nào mà có danh sách users bao gồm id của userOwn (người gửi) và friendUser (người nhận)
                    .whereArrayContains("users", userOwn.getUserId())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            Chat chat;
                            for (QueryDocumentSnapshot doc : value) {//Duyệt qua danh sách các document tìm được
                                chat = doc.toObject(Chat.class);//Ép kiểu sang object Chat
                                if(chat.getUsers().contains(friendUser.getUserId())){
                                    chatId = chat.getId();//Gắn giá trị cho chatId
                                    LoadMessage();//Load ra danh sách tin nhắn
                                    break;
                                }

                            }
                        }
                    });
        } else {//Trường hợp có chatId
            chatId = friendUser.getChatId();
            LoadMessage();//Load ra danh sách tin nhắn
        }

    }


    //Thêm các sự kiện cho các control cần thiết
    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {
        //Xử lý sự kiện khi nhập tin nhắn vào khung chat
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
        });//nút để mở màn chức năng ghi âm trong đoạn chat
        btn_hold_to_rec_sound.setOnTouchListener(new View.OnTouchListener() {//nút giữ để ghi âm
            @Override
            public boolean onTouch(View v, MotionEvent event) {//sự kiện người dùng chạm vào màn hình
                float tempX = event.getX();//lấy tọa độ x mà người dùng chạm vào màn hình.
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//nếu người dùng đang chạm vào màn hình
                    startRecording();//nếu người dùng giữ màn hình thì thu âm thanh
                } else if (event.getAction() == MotionEvent.ACTION_UP) {//nếu người dùng rời tay khỏi màn hình
                    pauseRecording();//dừng ghi âm khi người dùng không còn nhấn giữ
                    if (tempX <= -120) {//nếu nơi người dùng thả tay ra cách điểm chạm ban đầu là 120 về phía trái màn hình
                        //xóa ghi âm không gửi đi
                        fileAudioPath = null;
                        btn_cancel_rec.setVisibility(GONE);//ẩn nút xóa
                        btn_hand_rec.setVisibility(GONE);//ẩn nút ghi âm rãnh tay
                    } else {
                        onClickSendMessage();//nếu người dùng không thả vào vùng xóa thì file ghi âm sẽ được gửi đi
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {//nếu người dùng đang chạm và di chuyển trong màn hình
                    if (tempX <= -120) {//nếu người dùng giữ tay trên màn hình cách điểm chạm đầu tiên 120
                        btn_cancel_rec.setVisibility(View.VISIBLE);//ko ẩn nút xóa âm thanh
                        btn_hand_rec.setVisibility(View.VISIBLE);//không ẩn nút ghi âm rãnh tay
                    } else {
                        btn_cancel_rec.setVisibility(GONE);//nếu không nằm trong  vùng xóa ghi âm thì sẽ ẩn nút không cần thiết
                        btn_hand_rec.setVisibility(GONE);//nếu không nằm trong  vùng xóa ghi âm thì sẽ ẩn nút không cần thiết
                    }
                }
                return true;
            }
        });
    }

    private void pauseRecording() {
        txt_instruction_rec.setText("Nhấn giữ để ghi âm");//hiển thị nút ghi âm báo là nhấn giữ nút thu để ghi âm
        mRecorder.stop();//dừng ghi âm
        mRecorder.release();//release âm thanh để xuất file
        mRecorder = null;//gán rỗng mRecoder
    }

    private void startRecording() {
        txt_instruction_rec.setText("Thả ra để gửi, di chuyển sang trái để xóa");//set Text thả ra gửi file âm thanh
        fileAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();//lấy đường dẫn file âm thanh
        fileAudioPath += "/AudioRecording.3gp";//file tên là AudioRecording với định dạng là 3gp

        mRecorder = new MediaRecorder();//tạo mới biến mRecoder

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//lấy nguồn thu âm từ mic

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//chỉnh file audio định dạng là 3gp

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//encode theo AMR_NB

        mRecorder.setOutputFile(fileAudioPath);//thiết lập đường dẫn file audio khi xuất ra
        try {
            mRecorder.prepare();//chuẩn bị cho quy trình ghi âm
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
        mRecorder.start();//bắt đầu ghi âm
    }

    private void showRecordSoundWidget() {
        if (checkPermissions()) {//check có cấp quyền lưu trữ chưa
            if (linear_rec_sound.getVisibility() == View.VISIBLE) {//nếu widget ghi âm đã hiển thị thì ẩn widget đó đi,
                // giảm chiều dài bằng 0
                linear_rec_sound.setVisibility(GONE);
                linear_rec_sound.getLayoutParams().height = 0;
                round_button.getLayoutParams().height = 0;
                btn_hold_to_rec_sound.getLayoutParams().height = 0;
            } else if (linear_rec_sound.getVisibility() == GONE) {//nếu widget ghi âm đã ẩn thì hiển thị widget đó đi,
                // tăng chiều dài, cao vừa mức để nhìn thấy widget đó
                linear_rec_sound.setVisibility(View.VISIBLE);
                linear_rec_sound.getLayoutParams().height = 500;
                round_button.getLayoutParams().height = 200;
                btn_hold_to_rec_sound.getLayoutParams().height = 200;
            }
        } else {
            requestPermissions();//yêu cầu cấp quyền nếu check quyền chưa được cấp
        }

    }

    private void addControl() {
        btnBack = findViewById(R.id.btn_back);//Gắn phần tử btn_back trên layout cho biến
        btnCall = findViewById(R.id.btn_call);//Gắn phần tử btn_call trên layout cho biến
        btnVideoCall = findViewById(R.id.btn_video_call);//Gắn phần tử btn_video_call trên layout cho biến
        btnChatSetting = findViewById(R.id.btn_chat_setting);//Gắn phần tử chat_setting trên layout cho biến
        btnEmoji = findViewById(R.id.btn_emoji_bottom_sheet);//Gắn phần tử btn_emoji_bottom_sheet trên layout cho biến
        btnMoreHoz = findViewById(R.id.btn_more_horizontal);//Gắn phần tử btn_more_horizontal trên layout cho biến
        btnRecSound = findViewById(R.id.btn_rec_sound);//Gắn phần tử btn_rec_sound trên layout cho biến
        btnGetImage = findViewById(R.id.btn_get_image);//Gắn phần tử btn_get_image trên layout cho biến
        btnSend = findViewById(R.id.btn_send);//Gắn phần tử btn_send trên layout cho biến
        txtUserName = findViewById(R.id.txt_user_name);//Gắn phần tử txt_user_name trên layout cho biến
        txtOnlineStatus = findViewById(R.id.txt_online_status);//Gắn phần tử txt_online_status trên layout cho biến
        txtBodyMessage = findViewById(R.id.txt_body_message);//Gắn phần tử txt_body_message trên layout cho biến
        chat_bar = findViewById(R.id.chat_bar);//Gắn phần tử chat_bar trên layout cho biến
        recyclerViewMessageChat = findViewById(R.id.recyclerViewMessageChat);//Gắn phần tử recyclerViewMessageChat trên layout cho biến
        layoutImgSendMessage = findViewById(R.id.layoutImgSendMessage);//Gắn phần tử layoutIngSendMessage trên layout cho biến
        imgSendMessage = findViewById(R.id.imgSendMessage);//Gắn phần tử imgSendMessage trên layout cho biến
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//Khai báo một LinearLayoutManager để thiết lập layout cho RecyclerView
        layoutManager.setStackFromEnd(true);//Thứ tự load ra danh sách tin nhắn đi từ dưới lên
        recyclerViewMessageChat.setLayoutManager(layoutManager);//Gắn layout manager cho recyclerViewMessageChat
        linear_rec_sound = findViewById(R.id.linear_rec_sound);//gắn phần tử vùng chức năng ghi âm trong layout cho biến
        round_button = findViewById(R.id.round_button);//gắn phần tử nút dạng hình tròn vào biến layout
        btn_hold_to_rec_sound = findViewById(R.id.btn_hold_to_rec_sound);//gắn phần tử nút giữ ghi âm cho biến layout
        txt_instruction_rec = findViewById(R.id.txt_instruction_rec);//gắn text view hướng dẫn cách ghi âm
        btn_cancel_rec = findViewById(R.id.btn_cancel_rec);//gắn biến nút hủy bản ghi âm vào biến layout
        btn_hand_rec = findViewById(R.id.btn_hand_rec);//gắn biến ghi âm rãnh tay vào biến layout
    }

    //Xử lý sự kiện khi bấm nút Send
    public void onClickSendMessage() {
        Chat chat;//Khai báo object Chat
        if (chatId.equals("")) {//Nếu chatId rỗng tức 2 người chưa có nhắn chat
            DocumentReference chatRef = db.collection(Constants.CHAT_COLLECTION).document();//Tạo một DocumentReference liên kết tới database
            //mặc định khi tạo Document theo cách trên sẽ tạo id tự động
            chatId = chatRef.getId();//lấy id sau khi document được tạo
            //Khởi tạo object Chat với đầy đủ thông tin id, users gồm 2 người, thời gian và tin nhắn cuối cùng
            chat = new Chat(chatId, Arrays.asList(userOwn.getUserId(), friendUser.getUserId()), (new Date()).getTime(), "Tin nhắn đầu tiên");
            chatRef.set(chat);//set dữ liệu của chat cho document
            LoadMessage();//gọi hàm load tin nhắn
        }
        UUID uuid = UUID.randomUUID();//Tạo 1 uuid ngẫu nhiên,dùng uuid vì id này đảm bảo không trùng lập
        if (layoutImgSendMessage.getVisibility() == View.VISIBLE) {//upload ảnh
            FirebaseStorage storage = FirebaseStorage.getInstance();//Khai báo storage liên kết tới Storage trên Firebase
            StorageReference storageRef = storage.getReference().child("IMAGES");//Khai báo StorageReference chỉ đến nhánh IMAGES
            StorageReference filePath = storageRef.child(uuid.toString());// Khai báo 1 đường đẫn trên StorageReference
            uploadTask = filePath.putFile(fileImageSend);//Đẩy file lên database
            uploadTask.continueWithTask(new Continuation() {//Theo dõi tiến trình upload file
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {//Nếu upload lỗi
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();//Trả về đường dẫn của file sau khi upload thành công
                }
            }).addOnCompleteListener(new OnCompleteListener() {//Xử lý sau khi upload xong
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        String path = task.getResult().toString();//Lấy đường dẫn tới file vừa upload xong
                        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), path,
                                new Date().getTime(), -1, Constants.IMAGE);//Tạo 1 object Message chứa nội dung tin nhắn
                        db.collection(Constants.MESSAGE_COLLECTION)//Lấy collection MESSAGES trong database
                                .document(chatId)//Lấy đến document có id là chatId
                                .collection(Constants.SUBMESSAGE_COLLECTION)//Lấy một sub collection SUBMESSAGE chứa danh sách tin nhắn
                                .document(message.getId()).set(message);//Gắn thông tin của object message vào Database

                        db.collection(Constants.CHAT_COLLECTION)
                                .document(chatId)
                                .update("lastmessage", "[Hình ảnh]");//Cập nhật lại thông tin của document Chat trên database
                        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//Tạo một DocumentReference liên kết tới database
                        DocumentReference subuploadRef = uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document();//Tạo một DocumentReference liên kết tới database
                        Upload newUpload = new Upload();
                        newUpload.setUrl("IMAGES/"+uuid.toString());
                        newUpload.setUserId(userOwn.getUserId());
                        newUpload.setDate(new Date().getTime());
                        subuploadRef.set(newUpload);
                    }
                }
            });
            txtBodyMessage.setText("");//Gắn lại chuỗi rỗng cho khung chat sau khi tin được gửi đi
            imgSendMessage = null;//Gắn lại giá trị null cho hình ảnh sau khi gửi đi
            fileImageSend = null;//Gắn lại giá trị null cho file ảnh được chọn sau khi gửi đi
            layoutImgSendMessage.setVisibility(GONE);//ẩn đi layout chứa ảnh được chọn
        } else if (fileAudioPath != null) {//kiểm tra đường dẫn có trống không nếu ko trống thì đó là 1 tin nhắn âm thanh và sẽ được gửi đi ngay
            FirebaseStorage storage = FirebaseStorage.getInstance();//Khai báo storage liên kết tới Storage trên Firebase
            StorageReference storageRef = storage.getReference().child("audios");//Khai báo StorageReference chỉ đến nhánh IMAGES
            StorageReference filePath = storageRef.child(uuid.toString());// Khai báo 1 đường đẫn trên StorageReference
            fileAudioSend=Uri.fromFile(new File(fileAudioPath));//lấy uri từ file audio đã được lưu
            uploadTask = filePath.putFile(fileAudioSend);//Đẩy file lên database
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {//Nếu upload lỗi
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();//Trả về đường dẫn của file sau khi upload thành công
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        String path = task.getResult().toString();//Lấy đường dẫn tới file vừa upload xong
                        Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), path,
                                new Date().getTime(), -1, Constants.SOUND);//Tạo 1 object Message chứa nội dung tin nhắn dạng âm thanh
                        db.collection(Constants.MESSAGE_COLLECTION)//Lấy collection MESSAGES trong database
                                .document(chatId)//Lấy đến document có id là chatId
                                .collection(Constants.SUBMESSAGE_COLLECTION)//Lấy một sub collection SUBMESSAGE chứa danh sách tin nhắn
                                .document(message.getId()).set(message);//Gắn thông tin của object message vào Database
                        db.collection(Constants.CHAT_COLLECTION)
                                .document(chatId)
                                .update("lastmessage", "[Âm thanh]");//Cập nhật lại thông tin của document Chat trên database
//                        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//Tạo một DocumentReference liên kết tới database
//                        DocumentReference subuploadRef = uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document();//Tạo một DocumentReference liên kết tới database
//                        Upload newUpload = new Upload();
//                        newUpload.setUrl("audios/"+uuid.toString());
//                        newUpload.setUserId(userOwn.getUserId());
//                        newUpload.setDate(new Date().getTime());
//                        subuploadRef.set(newUpload);
                    }
                }
            });
            fileAudioPath = null;//xóa path
            fileAudioSend = null;//xóa file âm thanh được lưu trong biến file audio sau khi được gửi
        } else {
            //Khởi tạo một message chứa nội dung tin nhắn
            Message message = new Message(uuid.toString(), userOwn.getUserId(), friendUser.getUserId(), txtBodyMessage.getText().toString(),
                    new Date().getTime(), -1, Constants.TEXT);
            //truy cập đến Collection MESSAGES, document chatId, subcollection SUBMESSAGE
            db.collection(Constants.MESSAGE_COLLECTION)
                    .document(chatId)
                    .collection(Constants.SUBMESSAGE_COLLECTION)
                    .document(message.getId()).set(message);//Đưa thông tin của message thành 1 document trên Database
            db.collection(Constants.CHAT_COLLECTION)
                    .document(chatId)
                    .update("lastmessage", message.getContent());//Cập nhập lại field lassmessage của Chat
            txtBodyMessage.setText("");//Gắn lại chuỗi rỗng cho khung nhập tin nhắn sau khi gửi đi
        }


    }

    //Hàm xử lý load tin nhắn
    private void LoadMessage() {
        if (chatId == null || chatId.equals(""))//Nếu chatId null hoặc là chuỗi rỗng thì dừng xử lý
            return;
        messageAdapter = new MessageAdapter(messageList, ChatActivity.this, chatId);//Khởi tạo 1 Message Adapter đảm nhiệm hiển thị danh sách tin nhắn
        //Thực hiện truy vấn đến collection MESSAGES
        db.collection(Constants.MESSAGE_COLLECTION)
                .document(chatId)
                .collection(Constants.SUBMESSAGE_COLLECTION)
                .orderBy("time")//Sắp xếp các tin nhắn theo thứ tự thời gian
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override//Xử lý khi truy vấn xong
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageList.clear();//Dọn sạch danh sách tin nhắn cũ
                        for (QueryDocumentSnapshot doc : value) {//Duyệt qua từng tin nhắn
                            Message message = doc.toObject(Message.class);//Mapping từ document sang object Message
                            MessageChat messageChat;//Khai báo 1 object MessageChat, có tác dụng chứa thông tin để hiển thị lên cho người dùng
                            if (message.getSender().equals(userOwn.getUserId()))//nếu là người gửi là mình
                            {
                                //Gắn Avatar là của mình
                                messageChat = MessageMapping.EntityToMessageChat(message, myAvatar);
                            } else {
                                //Gắn Avatar là của người nhận
                                messageChat = MessageMapping.EntityToMessageChat(message, friendAvatar);
                            }
                            messageList.add(messageChat);//Thêm tin nhắn vào danh sách tin nhắn
                        }
                        recyclerViewMessageChat.setHasFixedSize(true);//thiết lập cho phép tự tối ưu kích thước hiển thị
                        recyclerViewMessageChat.setAdapter(messageAdapter);//set adapter
                        //thiết lập bộ nhớ cache cho recycler view
                        recyclerViewMessageChat.setItemViewCacheSize(50);
                        recyclerViewMessageChat.setDrawingCacheEnabled(true);
                        recyclerViewMessageChat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        messageAdapter.notifyDataSetChanged();//Thông báo đến adapter là dữ liệu có thay đổi để Adapter hiển thị lại danh sách mới
                        recyclerViewMessageChat.smoothScrollToPosition(messageList.size());//Thiết lập độ mượt khi cuộn
                    }
                });
    }

    //Xử lý chọn hình ảnh từ thiết bị
    void imageChooser() {
        Intent i = new Intent();//Khởi tạo 1 intent
        i.setType("image/*");//thiết lập loại dữ liệu sẽ chọn
        i.setAction(Intent.ACTION_GET_CONTENT);//Thiết lập loại hành động là chọn
        mStartForResult.launch(Intent.createChooser(i, "Select Picture"));//Khởi chạy Intent với tiêu đề Select Picture
    }

    //Khai báo 1 RegisterForActivityResult để xử lý khi chọn ảnh xong
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();//Lấy dữ liệu được trả về từ intent sau khi chọn file xong
                        fileImageSend = intent.getData();//Lấy Uri của ảnh
                        if (null != fileImageSend) {//kiểm tra nếu khác null
                            InputStream imageStream = null;//Khai báo imageStream để chứa dữ liệu ảnh
                            try {
                                imageStream = getContentResolver().openInputStream(fileImageSend);//load ảnh từ uri
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);//Decode từ stream sang dữ liệu ảnh dạng bitmap
                            imgSendMessage.setImageBitmap(selectedImage);//set dữ liệu ảnh cho imgSendMessage
                            layoutImgSendMessage.setVisibility(View.VISIBLE);//Hiện lên layout chứa ảnh được chọn

                            btnSend.getLayoutParams().width = 60;//Điều chỉnh kích thước nút gửi
                            btnMoreHoz.setVisibility(GONE);//Ẩn btn MoreHoz
                            btnRecSound.setVisibility(GONE);//Ẩn btn RecSound
                            btnGetImage.setVisibility(GONE);//Ẩn btn GetImage
                            btnSend.setVisibility(View.VISIBLE);//Ẩn btn Send
                        }

                    }
                }
            });

    //Xử lý sự kiện huỷ gửi ảnh
    public void onClickCancelSendImage(View view) {
        imgSendMessage.setImageBitmap(null);//Gắn giá trị null cho imgSend
        layoutImgSendMessage.setVisibility(GONE);
    }

    public boolean checkPermissions() {
        //yêu cầu cấp 3 quyền lưu trữ bộ nhớ để lưu file âm thanh
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        //yêu cầu cấp quyền sẽ hiển thị cửa sổ hệ thống
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}