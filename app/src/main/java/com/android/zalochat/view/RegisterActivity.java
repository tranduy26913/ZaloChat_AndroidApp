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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class RegisterActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button btnRegister;
    EditText txtPhone,txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = findViewById(R.id.btnRegister);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword =findViewById(R.id.txtPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterAccount();
            }
        });
    }

    private void RegisterAccount(){
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();

        User user = new User(phone,password);
        final DatabaseReference users = database.getReference("USERS").child(phone);

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // chua co user voi username duoc nhap
                if (dataSnapshot.getValue() == null) {



                    // them user vao nhanh Users
                    users.setValue(user, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            // hoc vien tu viet va kiem tra su kien loi va thanh cong
                            if (databaseError == null) {
                                String username = databaseReference.getRef().getKey();
                                Intent intent = new Intent();
                                intent.putExtra("data", username);
                                setResult(999, intent);
                                Toast.makeText(RegisterActivity.this,
                                        "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
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
}