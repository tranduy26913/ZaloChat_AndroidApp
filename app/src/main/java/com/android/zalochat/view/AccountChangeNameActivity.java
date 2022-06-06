package com.android.zalochat.view;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class AccountChangeNameActivity extends AppCompatActivity {
    private EditText newName;//Liên kết đến phần tử EditText Nhập tên mới
    private Button UpdateBtn; // Nút cập nhật tên mới
    private User userOwn;//Tài khoản của mình
    private FirebaseFirestore database;// database firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_name);// hiển thị activity activity_change_name
        database = FirebaseFirestore.getInstance(); // lấy ra instance của FirebaseFirestore gắn vào database
        setView(); //Cài đặt các Element
        setEvent(); //gắn Event cho các Element
    }

    private void setView() {
        this.UpdateBtn = findViewById(R.id.btnEditName);// Gắn nút Update tên cho biến
        this.newName = findViewById(R.id.editName);// Gắn nút EditText tên cho biến
    }

    private void setEvent() {
        this.UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Lấy ra bộ nhớ SharedPreferences
                String jsonUser = ref.getString(Constants.USER_JSON, "");//lấy ra chuỗi USER_JSON
                try {
                    Gson gson  = new Gson();
                    userOwn = gson.fromJson(jsonUser,User.class);//Convert sang kiểu User
                }catch (Exception ex) {
                    //GotoLogin();
                }
                String name = newName.getText().toString().trim(); //Lấy từ edit ra tên mới và thực hiện trim bỏ khoảng trắng thừa
                if(name.length()>0 &&name.length()<150){//Kiểm tra độ dài của tên vừa lấy
                    SharedPreferences.Editor prefedit
                            = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();//Lấy ra trình sửa chửa SharedPreferences
                    userOwn.setFullname(name); // gắn tên mới vào user đã lấy từ SharedPreferences
                    Gson gson = new Gson();
                    jsonUser = gson.toJson(userOwn);//Convert sang json String
                    prefedit.putString(Constants.USER_JSON, jsonUser);// Lưu lại Json sau sửa đổi
                    prefedit.apply();// Xác nhận sửa đội
                    database.collection(Constants.USER_COLLECTION).document(userOwn.getUserId()).update("fullname",userOwn.getFullname())// Update lại User sau khi sửa đổi với Id lấy từ trước.
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {//Thành công thì chạy vào đây
                            Toast.makeText(AccountChangeNameActivity.this, "Đổi tên thành công", Toast.LENGTH_SHORT).show();//Tạo toast thông báo đổi tên thành công
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {//Thất bại thì chạy vào đây
                                    Toast.makeText(AccountChangeNameActivity.this, "Có lỗi trong việc cập nhật tên ở database", Toast.LENGTH_SHORT).show();//Thông báo lỗi ở database
                                }
                            });

                    Goback();//Trở về trang trước
                }
                else
                {
                    Toast.makeText(AccountChangeNameActivity.this, "Tên không hợp lệ", Toast.LENGTH_SHORT).show();//Tạo toast thông báo tên không hợp lệ với điều kiện ở trên
                }
            }
        });
    }
    private void Goback() {
        Intent intent = new Intent(this, AccountSettingActivity.class);//Tạo intent AccountSettingActivity
        startActivity(intent);//Khởi chyaj intent
        finish();//kết thúc activity này
    }
}