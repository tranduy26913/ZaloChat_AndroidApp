package com.android.zalochat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.zalochat.model.Upload;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.LoginActivity;
import com.android.zalochat.view.fragment.AccountFragment;
import com.android.zalochat.view.fragment.MessageFragment;
import com.android.zalochat.view.fragment.PhoneBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private SearchView searchViewMain;
    SharedPreferences sharedPreferences;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    User userOwn;

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment;
        switch (item.getItemId()) {
            case R.id.miMessage: {
                selectedFragment = new MessageFragment();
                break;
            }
            case R.id.miPhonebook: {
                selectedFragment = new PhoneBookFragment();
                break;
            }
            default:
                selectedFragment = new AccountFragment();
                break;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutMain, selectedFragment).commit();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckLogin();
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(navListener);
        bottomNavigation.setSelectedItemId(R.id.miMessage);
        searchViewMain = findViewById(R.id.searchViewMain);
        searchViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToSearch();
            }
        });
        searchViewMain.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToSearch();
            }
        });

        SharedPreferences ref = getSharedPreferences(Constants.SHAREPREF_USER,MODE_PRIVATE);//Khai báo SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy json của user từ sharedPreferences
        try {
            Gson gson  = new Gson();//Gson thực hiện các xử lý liên quan đến json
            userOwn = gson.fromJson(jsonUser,User.class);//CHuyển từ json sang object User
            CheckImage();
        }catch (Exception ex) {
            //GotoLogin();
        }


    }

    private void CheckLogin() {
        SharedPreferences pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);
        Gson gson = new Gson();
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//lấy json user từ share pref
        if (userOwnJson.equals("")) {
            GoToLogin();
        } else {

            try {
                User userOwn = gson.fromJson(userOwnJson, User.class);//parse sang class User
            } catch (Exception ex) {
                GoToLogin();
                Toast.makeText(this, "Phiên làm việc đã hết. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    private void CheckImage(){
        Long now = new Date().getTime();
        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//Tạo một DocumentReference liên kết tới database

        uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).whereLessThan("date",now - /*100*24*60*60*14*/2*60*100).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                for (QueryDocumentSnapshot doc:value) {//duyệt qua từng document kết quả
                    Upload upload = doc.toObject(Upload.class);
                    try{
                        StorageReference desertRef = storageRef.child(upload.getUrl());
                        desertRef.delete();
                    }
                    catch (Exception e){

                    }
                    uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document(doc.getId()).delete();
                }
            }
        });
    }
//    private void CheckSound(){
//        Long now = new Date().getTime();
//        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//Tạo một DocumentReference liên kết tới database
//
//        uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).whereLessThan("date",now - 100*24*60*60*14).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageRef = storage.getReference();
//                for (QueryDocumentSnapshot doc:value) {//duyệt qua từng document kết quả
//                    Upload upload = doc.toObject(Upload.class);
//                    try{
//                        StorageReference desertRef = storageRef.child(upload.getUrl());
//                        desertRef.delete();
//                    }
//                    catch (Exception e){
//
//                    }
//                    uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document(doc.getId()).delete();
//                }
//            }
//        });
//    }
}