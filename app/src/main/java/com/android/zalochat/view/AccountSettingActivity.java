package com.android.zalochat.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.zalochat.R;
import com.android.zalochat.SearchActivity;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.fragment.AccountFragment;
import com.android.zalochat.view.fragment.MessageFragment;
import com.android.zalochat.view.fragment.PhoneBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class AccountSettingActivity extends AppCompatActivity {
    private ConstraintLayout changePassword;
    private ImageView imageView;//Liên kết đến phần tử layoutcontraint Đổi mật khẩu
    private TextView textName;//Liên kết đến tên người dùng
    private TextView tvPhoneDescription;//Liên kết đến hiển thị số điện thoại người dùng
    private ConstraintLayout changeName;//Liên kết đến phần tử layoutcontraint Đổi tên người dùng
    private ConstraintLayout logout;//Liên kết đến phần tử layoutcontraint Đăng xuất
    private User userOwn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);// hiển thị activity activity_account_setting
        setView();// gắn các view với biến
        setEvent();//cài dặt event cho view
        SharedPreferences ref = this.getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);// Lấy ra vùng nhớ SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//lấy ra đối tượng user hiên tại đăng nhập
        try {
            Gson gson  = new Gson();
            userOwn = gson.fromJson(jsonUser,User.class);// Convert json sang User
        }catch (Exception ex) {
            //GotoLogin();
        }
        Picasso.get().load(userOwn.getAvatar()).into(this.imageView);// Tải ra hình ảnh người đang dùng
        this.textName.setText(userOwn.getFullname());//tải ra tên người dùng đang dùng
        this.tvPhoneDescription.setText(userOwn.getPhone());//Hiển thị sđt người dùng
    }

    private void setView() {
        this.changePassword = findViewById(R.id.layoutChangePassword);// set biến ứng với layout ở view
        this.changeName = findViewById(R.id.layoutChangeName);// set biến ứng với layout ở view
        this.logout = findViewById(R.id.layoutLogout);// set biến ứng với layout ở view
        this.textName = findViewById(R.id.tvNameProfile);// set biến ứng với tên ở view
        this.imageView = findViewById(R.id.imageViewInfo);// set biến ứng với avatar ở view
        this.tvPhoneDescription = findViewById(R.id.tvPhoneDescription);//Set biến với số đt người dùng hiển thị
    }

    private void setEvent() {
        this.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToPassword();
            }// Nếu view changePassword được chọn thì chạy hàm GoToPassword()
        });

        this.changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToChangeName();
            }
            // Nếu view changeName được chọn thì chạy hàm GoToChangeName()
        });

        this.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nếu layout Logout
                SharedPreferences.Editor prefedit
                        = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();// Lấy ra trình chỉnh sửa SharedPreferences
                prefedit.remove(Constants.USERID); //xóa id đang đăng nhập hiện tại
                prefedit.remove(Constants.USER_JSON);//xóa user đang đăng nhập hiện tại
                prefedit.apply();//Xác nhận sau sửa đổi
                GoToLogin();// Chuyển lại vào màn hình login
            }
        });
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoChangeAvatar();
            }
            // Nếu view imageView được chọn thì chạy hàm GoChangeAvatar()
        });
    }
    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);//Tạo intent cho LoginActivity
        startActivity(intent);//Khởi chạy intent
        finish();//kết thúc activity này
    }
    private void GoChangeAvatar() {
        Intent intent = new Intent(this, ChangeAvatarActivity.class);//Tạo intent cho ChangeAvatarActivity
        startActivity(intent);//Khởi chạy intent
        finish();//kết thúc activity này
    }
    private void GoToChangeName() {
        Intent intent = new Intent(this, AccountChangeNameActivity.class);//Tạo intent cho AccountChangeNameActivity
        startActivity(intent);//Khởi chạy intent
        finish();//kết thúc activity này
    }
    private void GoToPassword() {
        Intent intent = new Intent(this, AccountChangePasswordActivity.class);//Tạo intent cho AccountChangePasswordActivity
        startActivity(intent);//Khởi chạy intent

    }


}