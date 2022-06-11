package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.MainActivity;
import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.UtilPassword;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    EditText txtPhoneLogin, txtPasswordLogin;//Liên kết tới phần tử EditText số điện thoại và mật khẩu
    CountryCodePicker ccp;//Liên kết tới phần tử chọn mã vùng
    private FirebaseFirestore database = FirebaseFirestore.getInstance();//Liên kết tới Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//thiết lập view layout activity_login
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin);//gắn edittext mật khẩu cho biến
        txtPhoneLogin = findViewById(R.id.txtPhoneLogin);//gắn edittext số điện thoại cho biến
        ccp = findViewById(R.id.ccp);//gắn country code picker cho biến
    }

    public void onClickLogin(View view) {
        ProgressDialog progress = new ProgressDialog(this);//Khai báo 1 process dialog
        progress.setMessage("Đang đăng nhập...");//Đặt nội dung cho dialog
        progress.setCancelable(false); // thiết lập không thể huỷ process
        progress.show();//hiển thị dialog


        String phone = ccp.getDefaultCountryCodeWithPlus() + txtPhoneLogin.getText().toString().trim();//lấy số điện thoại do người dùng nhập vào kèm với mã vùng
        String password = txtPasswordLogin.getText().toString();//lấy mật khẩu do người dùng nhập vào

        database.collection(Constants.USER_COLLECTION)
                .document(phone)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    //lấy ra 1 document thuộc collection USERS trong database
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);//Mapping từ DocumentSnapshot sang object User
                        if (UtilPassword.verifyPassword(password, user.getPassword())) {//Xác nhận mật khẩu đã mã hoá
                            if (user.isActive()) {//kiểm tra xem tài khoản đã kích hoạt chưa
                                SharedPreferences.Editor prefedit
                                        = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();//Lấy Shared Preferences Editor
                                Gson gson = new Gson();//Khai báo object Gson để thực hiện các thao tác liên quan tới json
                                String jsonUser = gson.toJson(user);//chuyển object user sang chuỗi json
                                prefedit.putString(Constants.USERID, user.getUserId());//đưa userid vào Shared Preferences
                                prefedit.putString(Constants.USER_JSON, jsonUser);//đưa chuỗi json chứa thông tin object vào Shared Preferences
                                prefedit.apply();//xác nhận và lưu thông tin vừa thay đổi
                                progress.dismiss();///dừng process dialog
                                GoToMainActivity();//Chuyển đến Main Activity
                                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();//thông báo đăng nhập thành công
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.user_inactive), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.wrong_phone), Toast.LENGTH_SHORT).show();
                    }

                }
                progress.dismiss();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {//được gọi khi huỷ quá trình đăng nhập
                Toast.makeText(LoginActivity.this, getString(R.string.login_false), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });


    }

    private void GoToMainActivity() {//Chuyển từ login Activity sang main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onTextViewSendMailClick(View view) {//Khi bấm vào nút gửi mã kích hoạt
        //Chuyển từ login activity sang Verify phone activity
        Intent intent = new Intent(this, VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", txtPhoneLogin.getText().toString());//truyền số điện thoại sang Verify phone activity
        startActivity(intent);
    }

    public void onClickGoToRegister(View view) {//Khi bấm vào nút đăng ký
        //Chuyển từ login activity sang Register activity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onTextViewForgotPassword(View view) {
        //Chuyển từ login activity sang Register activity
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("phonenumber", txtPhoneLogin.getText().toString());//truyền số điện thoại sang Verify phone activity
        startActivity(intent);
    }
}