package com.android.zalochat.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.zalochat.R;

public class LoginActivity extends AppCompatActivity {

    EditText txtPhoneLogin,txtPasswordLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin);
        txtPhoneLogin = findViewById(R.id.txtPhoneLogin);
    }

    public void onClickLogin(View view) {

    }

    public void onTextViewSendMailClick(View view) {
        Intent intent = new Intent(this,VerifyPhoneActivity.class);
        intent.putExtra("phonenumber",txtPhoneLogin.getText().toString());
        startActivity(intent);
    }
}