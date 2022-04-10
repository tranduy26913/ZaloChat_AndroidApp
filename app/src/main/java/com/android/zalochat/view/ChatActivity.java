package com.android.zalochat.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zalochat.R;

public class ChatActivity extends AppCompatActivity {
    ImageButton btnBack, btnCall, btnVideoCall, btnChatSetting, btnEmoji, btnMoreHoz, btnRecSound, btnGetImage, btnSend;
    TextView txtUserName,txtOnlineStatus;
    EditText txtBodyMessage;
    LinearLayout chat_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addControl();
        addEvent();
        loadDataIntent();
    }

    private void loadDataIntent() {
        String fullname=getIntent().getStringExtra("fullname");
        txtUserName.setText(fullname);
    }


    private void addEvent() {
        txtBodyMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    btnMoreHoz.getLayoutParams().width=0;
                    btnRecSound.getLayoutParams().width=0;
                    btnGetImage.getLayoutParams().width=0;
                    btnSend.getLayoutParams().width=60;
                    btnMoreHoz.setVisibility(View.INVISIBLE);
                    btnRecSound.setVisibility(View.INVISIBLE);
                    btnGetImage.setVisibility(View.INVISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                }
                else if(charSequence.length()==0){
                    btnMoreHoz.setVisibility(View.VISIBLE);
                    btnRecSound.setVisibility(View.VISIBLE);
                    btnGetImage.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.INVISIBLE);
                    btnMoreHoz.getLayoutParams().width=50;
                    btnRecSound.getLayoutParams().width=50;
                    btnGetImage.getLayoutParams().width=50;
                    btnSend.getLayoutParams().width=0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnBack.setOnClickListener(view -> {
            finish();
        });

    }

    private void addControl() {
        btnBack=findViewById(R.id.btn_back);
        btnCall=findViewById(R.id.btn_call);
        btnVideoCall=findViewById(R.id.btn_video_call);
        btnChatSetting=findViewById(R.id.btn_chat_setting);
        btnEmoji=findViewById(R.id.btn_emoji_bottom_sheet);
        btnMoreHoz=findViewById(R.id.btn_more_horizontal);
        btnRecSound=findViewById(R.id.btn_rec_sound);
        btnGetImage=findViewById(R.id.btn_get_image);
        btnSend=findViewById(R.id.btn_send);
        txtUserName=findViewById(R.id.txt_user_name);
        txtOnlineStatus=findViewById(R.id.txt_online_status);
        txtBodyMessage=findViewById(R.id.txt_body_message);
        chat_bar=findViewById(R.id.chat_bar);
    }


}