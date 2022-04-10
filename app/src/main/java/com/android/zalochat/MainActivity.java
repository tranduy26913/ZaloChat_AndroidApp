package com.android.zalochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.zalochat.view.ChatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void chatPage(View view) {
        Intent intent=new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}