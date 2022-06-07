package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnterCodeOtpActivity extends AppCompatActivity {

    private String phonenumber;//Biến lưu số điện thoại cần kích hoạt
    private String verificationId;//Biến lưu verification id
    Button btnVerify;//Liên kết tới phần tử Button Verify
    EditText txtVerifyCode;//Liên kết tới phần tử TextView Verifycode

    FirebaseAuth mAuth;//Liên kết tới Firebase authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code_otp);//Thiết lập giao diện activity_enter_code_otp
        btnVerify = findViewById(R.id.btnVerify);//Gắn layout btnVerify cho biến
        txtVerifyCode = findViewById(R.id.txtVerifyCode);//Gắn layout txtVerifyCode
        mAuth = FirebaseAuth.getInstance();//Lấy instance của firebase authentication
        getDataIntent();//Lấy dữ liệu từ intent
    }
    //Lấy dữ liệu từ intent
    private void getDataIntent(){
        verificationId = getIntent().getStringExtra("verificationId");//Lấy giá trị verification từ intent gắn vào biến
        phonenumber = getIntent().getStringExtra("phonenumber");//Lấy giá trị phone number từ intent gắn vào biến
    }

    public void onClickVerify(View view){
        //Khai báo PhoneAuthCredential dùng để xác thực code otp
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txtVerifyCode.getText().toString());
signInWithPhoneAuthCredential(credential);//Đăng nhập với credential (Xác thực code otp)
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)//Gọi hàm Xác thực của Firebase Authentication
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//Nếu thành công
                            Toast.makeText(EnterCodeOtpActivity.this,"Xác thực thành công",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            ActiveUser(phonenumber);//Kích hoạt cho tài khoản trên firebase
                            GoToLogin(phonenumber);//Đi đến Login
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EnterCodeOtpActivity.this,"Xác thực thất bại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void ActiveUser(String phonenumber) {//Kích hoạt tài khoản
        FirebaseFirestore database = FirebaseFirestore.getInstance();//Khai báo database kết nối tới Firestore
        database.collection(Constants.USER_COLLECTION)
                .document(phonenumber)
                .update("active",true);//update lại field active = True của document User
    }

    private void GoToLogin(String phonenumber){//Mở activity Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("phonenumber",phonenumber);//Truyền giá trị phonenumber qua intent
        startActivity(intent);//Mở activity
    }

}