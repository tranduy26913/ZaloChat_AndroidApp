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
    public static User LoginUser(String phone,String password){
return  new User();
    }
}
