package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.UtilPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {
    private String phonenumber;//Biến lưu số điện thoại cần kích hoạt
    Button btnSendCode;//Liên kết với Button Send Otp Code
    EditText txtPhone;//Liên kết với EditText số điện thoại
    CountryCodePicker ccp;//Liên kết với Country Code Picker chọn mã vùng
    private static final String TAG = "PhoneAuthActivity";//biến TAG để phân loại các Log
    private ConstraintLayout layoutEnterPhoneForgot, layoutEnterCodeOtpForgot;
    Button btnVerify;//Liên kết tới phần tử Button Verify
    EditText txtVerifyCode;//Liên kết tới phần tử TextView Verifycode
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;//Callback của Phone Authentication
    private String mVerificationId;//Mã xác thực
    private PhoneAuthProvider.ForceResendingToken mResendToken;//Token để thực hiện gửi lại otp
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);//Thiết lập View layout activity_verify_phone
        btnSendCode = findViewById(R.id.btnSendCode);//Gắn phần tử Button Sendcode cho biến
        txtPhone = findViewById(R.id.txtPhone);//Gắn phần tử EditText vho biến
        ccp =findViewById(R.id.ccp);//Gắn phần tử Country Code Picker cho biến
        layoutEnterPhoneForgot = findViewById(R.id.layoutEnterPhoneForgot);
        layoutEnterCodeOtpForgot = findViewById(R.id.layoutEnterCodeOtpForgot);
        btnVerify = findViewById(R.id.btnVerify);//Gắn layout btnVerify cho biến
        txtVerifyCode = findViewById(R.id.txtVerifyCode);//Gắn layout txtVerifyCode
        getDataFromIntent();//Load dữ liệu từ Intent
        mAuth = FirebaseAuth.getInstance();//lấy ra instance của FirebaseAuth
    }

    private void getDataFromIntent() {//Lấy dữ liệu từ Intent
        //Lấy dữ liệu phonenumber từ intent
        phonenumber = getIntent().getStringExtra("phonenumber");
        txtPhone.setText(phonenumber);
    }


    public void onClickSendCode(View view) {//Xử lý sự kiện khi bấm nút gửi mã otp
        //Lấy số điện thoại và mã vùng
        phonenumber = ccp.getDefaultCountryCodeWithPlus()+txtPhone.getText().toString().trim();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //khi verify thành công
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                //signInWithPhoneAuthCredential(credential);
            }
            //Verify thất bại
            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.send_code_to_activate_fail), Toast.LENGTH_SHORT).show();
            }

            //Khi mã otp đã được gửi tới số điện thoại
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;

                GoToEnterCodeOtp(verificationId, phonenumber);
            }


        };
        //Build một option của Phone Auth
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Số điện thoại cần gửi mã
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian kết thúc gửi mã
                        .setActivity(this)                 // Thiết lập activity đang dùng
                        .setCallbacks(mCallbacks)          // Thiết lập mcallBacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void onClickVerify(View view){
        //Khai báo PhoneAuthCredential dùng để xác thực code otp
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, txtVerifyCode.getText().toString());
        signInWithPhoneAuthCredential(credential);//Đăng nhập với credential (Xác thực code otp)
    }

    //
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            ActiveUser(phonenumber);//Kích hoạt cho tài khoản trên firebase
                            GoToLogin(phonenumber);//Đi đến Login
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    //Đi đến trang nhập mã otp
    private void GoToEnterCodeOtp(String verificationId, String phonenumber) {
        layoutEnterPhoneForgot.setVisibility(View.GONE);
        layoutEnterCodeOtpForgot.setVisibility(View.VISIBLE);
    }

    private void ActiveUser(String phonenumber) {//Kích hoạt tài khoản
        FirebaseFirestore database = FirebaseFirestore.getInstance();//Khai báo database kết nối tới Firestore
        database.collection(Constants.USER_COLLECTION)
                .document(phonenumber)
                .update("password", UtilPassword.HashPassword("12345678"));//update lại field active = True của document User
        Toast.makeText(this, "Mật khẩu đã được Reset về 12345678. Vui lòng đăng nhập và đổi mật khẩu mới", Toast.LENGTH_SHORT).show();
    }

    private void GoToLogin(String phonenumber){//Mở activity Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("phonenumber",phonenumber);//Truyền giá trị phonenumber qua intent
        startActivity(intent);//Mở activity
    }

}