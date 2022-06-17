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
import com.android.zalochat.view.SearchActivity;
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

    private BottomNavigationView bottomNavigation;//Liên kết đến phần tử BottomNavigationView bottomNavigation trên layout
    private SearchView searchViewMain;//Liên kết đến phần tử SearchView searchViewMain trên layout
    SharedPreferences sharedPreferences;//Biến lưu SharedPreferences của ứng dụng
    private FirebaseFirestore db =FirebaseFirestore.getInstance();//Biến lưu database FirebaseFirestore
    private User userOwn;//Biến lưu tài khoản của người dùng

    private NavigationBarView.OnItemSelectedListener navListener = item -> {//Xử lý sự kiện chọn item trên navigation bar view
        Fragment selectedFragment;//Lưu trữ fragment mà người dùng lựa chọn
        switch (item.getItemId()) {
            case R.id.miMessage: {//Nếu chọn item Message trên navigation
                selectedFragment = new MessageFragment();//Khai báo 1 MessageFragment gắn cho selectedFragment
                break;
            }
            case R.id.miPhonebook: {//Nếu chọn item Phonebook trên navigation
                selectedFragment = new PhoneBookFragment();//Khai báo 1 PhoneBookFragment gắn cho selectedFragment
                break;
            }
            default://Nếu chọn item Account trên navigation
                selectedFragment = new AccountFragment();//Khai báo 1 AccountFragment gắn cho selectedFragment
                break;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutMain, selectedFragment).commit();//Thay đổi fragment chính thành fragment đang chọn
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckLogin();//Kiểm tra xem đã đăng nhập chưa
        setContentView(R.layout.activity_main);//Set giao diện cho activity
        bottomNavigation = findViewById(R.id.bottom_navigation);//Gắn phần tử bottomNavigation trên layout cho biến
        bottomNavigation.setOnItemSelectedListener(navListener);//Gắn sự kiện bấm vào item trên navigation bar
        bottomNavigation.setSelectedItemId(R.id.miMessage);//Gắn item được chọn mặc định miMessage để chọn fragment mặc định là MessageFragment
        searchViewMain = findViewById(R.id.searchViewMain);//Gắn phần tử searchViewMain trên layout cho biến
        searchViewMain.setOnClickListener(new View.OnClickListener() {//Gắn sự kiện onclick cho phần tử searchViewMain
            @Override
            public void onClick(View view) {
                GoToSearch();//Đi đến activity search
            }
        });
        searchViewMain.setOnSearchClickListener(new View.OnClickListener() {//Gắn sự kiện on Search Click cho phần tử searchViewMain
            @Override
            public void onClick(View view) {
                GoToSearch();//Đi đến activity search
            }
        });

        SharedPreferences ref = getSharedPreferences(Constants.SHAREPREF_USER,MODE_PRIVATE);//Khai báo SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy json của user từ sharedPreferences
        try {
            Gson gson  = new Gson();//Gson thực hiện các xử lý liên quan đến json
            userOwn = gson.fromJson(jsonUser,User.class);//CHuyển từ json sang object User
            CheckImage();//Kiểm tra xem hình ảnh hết hạn tồn tại chưa
        }catch (Exception ex) {
            //GotoLogin();
        }
    }

    private void CheckLogin() {
        SharedPreferences pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Lấy ra sharedPreference
        Gson gson = new Gson();//Khai báo gson để xử lý với json
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//lấy json user từ share pref
        if (userOwnJson.equals("")) {//Nếu chưa có tài khoản, tức là chưa đăng nhập
            GoToLogin();//đi đến Activity Login
        } else {
            try {
                User userOwn = gson.fromJson(userOwnJson, User.class);//parse sang class User
            } catch (Exception ex) {//Nếu xảy ra lỗi
                GoToLogin();//đi đến Activity Login
                Toast.makeText(this, "Phiên làm việc đã hết. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);//tạo intent để mở từ activity hiện tại tới activity Login
        startActivity(intent);//Mở activity login
        finish();//Đóng activity Main
    }

    private void GoToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);//tạo intent để mở từ activity hiện tại tới activity Login
        startActivity(intent);//Mở activity Search Activity
    }
    private void CheckImage(){
        Long now = new Date().getTime();//Khai báo thời gian hiện tại
        DocumentReference uploadRef = db.collection(Constants.UPLOAD_COLLECTION).document(userOwn.getUserId());//Tạo một DocumentReference liên kết tới database

        uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).whereLessThan("date",now - /*100*24*60*60*14*/2*60*100).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                FirebaseStorage storage = FirebaseStorage.getInstance();//Lấy instance của FirebaseStorage
                StorageReference storageRef = storage.getReference();//Lấy getReference của storage
                for (QueryDocumentSnapshot doc:value) {//duyệt qua từng document kết quả
                    Upload upload = doc.toObject(Upload.class);//Mapping từ QueryDocumentSnapshot sang object Upload
                    try{
                        StorageReference desertRef = storageRef.child(upload.getUrl());//ánh xạ tới file cần xoá trên database FireStorage
                        desertRef.delete();//Xoá đi file được ánh xạ
                    }
                    catch (Exception e){

                    }
                    uploadRef.collection(Constants.SUBUPLOAD_COLLECTION).document(doc.getId()).delete();//Xoá
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