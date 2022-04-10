package com.android.zalochat.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.zalochat.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}