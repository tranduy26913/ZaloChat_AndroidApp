package com.android.zalochat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        getData();
    }

    private void getData(){
        String fullName= getIntent().getStringExtra("FullName").toString();
        textView.setText("Chào mừng" + fullName);
    }
}