package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

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
                            ActiveUser(phonenumber);
                            GoToLogin(phonenumber);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EnterCodeOtpActivity.this,"Xác thực thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void ActiveUser(String phonenumber) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReference("USERS").child(phonenumber);
        database.collection(Constants.USER_COLLECTION)
                .document(phonenumber)
                .update("active",true);
    }

    private void GoToLogin(String phonenumber){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("phonenumber",phonenumber);
        startActivity(intent);
    }

}