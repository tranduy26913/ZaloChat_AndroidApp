package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class EnterCodeOtpActivity extends AppCompatActivity {

    private String phonenumber;
    private String verificationId;
    Button btnVerify;
    EditText txtVerifyCode;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code_otp);
        btnVerify = findViewById(R.id.btnVerify);
        txtVerifyCode = findViewById(R.id.txtVerifyCode);
        mAuth = FirebaseAuth.getInstance();
        getDataIntent();
    }

    private void getDataIntent(){
        verificationId = getIntent().getStringExtra("verificationId");
        phonenumber = getIntent().getStringExtra("phonenumber");
    }

    public void onClickVerify(View view){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txtVerifyCode.getText().toString());
signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EnterCodeOtpActivity.this,"Xác thực thành công",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            GoToLogin(phonenumber);
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EnterCodeOtpActivity.this,"Xác thực thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void GoToLogin(String phonenumber){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("phonenumber",phonenumber);
        startActivity(intent);
    }

}