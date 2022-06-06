package com.android.zalochat.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.UtilPassword;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class AccountChangePasswordActivity extends AppCompatActivity {
    private EditText OldPassword;//Liên kết đến phần tử EditText mật khẩu hiện tại
    private EditText NewPassword;//Liên kết đến phần tử EditText Mật khẩu mới
    private EditText ConfirmPassword;//Liên kết đến phần tử EditText Nhập lại mật khẩu mới
    private Button UpdateBtn; // Nút cập nhật password
    private User userOwn;//Tài khoản của mình
    private FirebaseFirestore database;// database firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_password);// hiển thị activity activity_change_password
        database = FirebaseFirestore.getInstance();// lấy ra instance của FirebaseFirestore gắn vào database
        setView();//Cài đặt các Element
        setEvent();//gắn Event cho các Element
    }

    private void setView() {
        this.OldPassword = findViewById(R.id.editOldPassword);// Gắn  edit text mật khẩu cũ  cho biến
        this.NewPassword = findViewById(R.id.editNewPassword);// Gắn  edit text mật khẩu mói  cho biến
        this.ConfirmPassword = findViewById(R.id.EditConfirmNewPassword);// Gắn  edit text nhập lại mật khẩu mói  cho biến
        this.UpdateBtn = findViewById(R.id.btnEditPassword);// Gắn nút edit cập nhật mật khẩu cho biến
    }

    private void setEvent() {
        this.UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = OldPassword.getText().toString();
                SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Lấy ra bộ nhớ SharedPreferences
                String jsonUser = ref.getString(Constants.USER_JSON, "");//lấy ra chuỗi USER_JSON
                try {
                    Gson gson  = new Gson();
                    userOwn = gson.fromJson(jsonUser,User.class);//Convert sang kiểu User
                }catch (Exception ex) {
                    //GotoLogin();
                }
                if (UtilPassword.verifyPassword(password, userOwn.getPassword())) {//Kiểm tra mật khẩu cũ đúng hay không
                    if (NewPassword.getText().toString().trim().equals(ConfirmPassword.getText().toString().trim())) {//So sánh 2 chuỗi mật khẩu mới và nhập lại mật khẩu mới khớp hay không
                        SharedPreferences.Editor prefedit
                                = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();//Lấy ra trình sửa chửa SharedPreferences
                        userOwn.setPassword(UtilPassword.HashPassword(NewPassword.getText().toString().trim()));//hash mật khẩu mới và gắn vào User đã lấy ra từ trước
                        Gson gson = new Gson();
                        jsonUser = gson.toJson(userOwn);//Convert sang json String
                        prefedit.putString(Constants.USER_JSON, jsonUser);// Lưu lại Json sau sửa đổi
                        prefedit.apply();// Xác nhận sửa đội
                        database.collection(Constants.USER_COLLECTION).document(userOwn.getUserId()).update("password",userOwn.getPassword()).// Update lại User sau khi sửa đổi với Id lấy từ trước.
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {//Thành công thì chạy vào đây
                                        Toast.makeText(AccountChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();//Tạo toast thông báo đổi mật khẩu thành công
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {//Thất bại thì chạy vào đây
                                        Toast.makeText(AccountChangePasswordActivity.this, "Có lỗi tỏng việc cập nhật mật khẩu ở database", Toast.LENGTH_SHORT).show();//Thông báo lỗi ở database
                                    }
                                });

                    } else {
                        Toast.makeText(AccountChangePasswordActivity.this, "Nhập lại mật khẩu không khớp", Toast.LENGTH_SHORT).show();//Tạo toast thông báo lỗi
                    }

                } else {
                    Toast.makeText(AccountChangePasswordActivity.this, "Mật khẩu hiện tại không giống", Toast.LENGTH_SHORT).show();//Tạo toast thông báo lỗi
                }
            }
        });
    }
}