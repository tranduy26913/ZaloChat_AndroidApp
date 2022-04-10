package com.android.zalochat.firebase.service;

import androidx.annotation.NonNull;

import com.android.zalochat.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserService {

    public static final String USERS = "USERS";

    public static final void SetObjectFromFirebaseToEntity(Object val1,Object val2){
        val1 = val2;
    }
    public static User LoginUser(String phone,String password){
        User user;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // truy vấn vào nhánh username mà người dùng nhập
        DatabaseReference users = firebaseDatabase.getReference("USERS").child(phone);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    User user = snapshot.getValue(User.class);
                    SetObjectFromFirebaseToEntity(user,user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    return  new User();
    }
}
