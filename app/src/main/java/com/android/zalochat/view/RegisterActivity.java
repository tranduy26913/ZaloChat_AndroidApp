package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.UtilPassword;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button btnRegister;
    EditText txtPhone,txtPassword,txtName;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = findViewById(R.id.btnRegister);
        txtName = findViewById(R.id.txtNameRegister);
        txtPhone = findViewById(R.id.txtPhoneRegister);
        txtPassword =findViewById(R.id.txtPasswordRegister);
        ccp = findViewById(R.id.ccp);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterAccount();
            }
        });
    }

    private void RegisterAccount(){
        String phone = ccp.getDefaultCountryCodeWithPlus()+  txtPhone.getText().toString().trim();
        String password = UtilPassword.HashPassword(txtPassword.getText().toString());
        String fullname = txtName.getText().toString();
        User user = new User(phone,password,fullname);

        final DatabaseReference users = database.getReference("USERS").child(phone);//lấy data

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    // them user vao nhanh Users
                    users.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(RegisterActivity.this,
                                        "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();

                                GoToVerifyPhoneNumber();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // username da ton tai, thong bao chon username khac
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GoToVerifyPhoneNumber() {
        Intent intent = new Intent(this,VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", txtPhone.getText().toString().trim());
        startActivity(intent);
    }

    public void onClickGoToLogin(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}