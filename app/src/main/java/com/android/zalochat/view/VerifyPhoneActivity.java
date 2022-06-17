package com.android.zalochat.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.zalochat.R;
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
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    Button btnSendCode;//Liên kết với Button Send Otp Code
    EditText txtPhone;//Liên kết với EditText số điện thoại
    CountryCodePicker ccp;//Liên kết với Country Code Picker chọn mã vùng
    private static final String TAG = "PhoneAuthActivity";//biến TAG để phân loại các Log

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;//Callback của Phone Authentication
    private String mVerificationId;//Mã xác thực
    private PhoneAuthProvider.ForceResendingToken mResendToken;//Token để thực hiện gửi lại otp
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);//Thiết lập View layout activity_verify_phone
        btnSendCode = findViewById(R.id.btnSendCode);//Gắn phần tử Button Sendcode cho biến
        txtPhone = findViewById(R.id.txtPhone);//Gắn phần tử EditText vho biến
        ccp =findViewById(R.id.ccp);//Gắn phần tử Country Code Picker cho biến
        getDataFromIntent();//Load dữ liệu từ Intent
        mAuth = FirebaseAuth.getInstance();//lấy ra instance của FirebaseAuth
    }

    private void getDataFromIntent() {//Lấy dữ liệu từ Intent
        //Lấy dữ liệu phonenumber từ intent
        String phonenumber = getIntent().getStringExtra("phonenumber");
        txtPhone.setText(phonenumber);
    }


    public void onClickVerify(View view) {//Xử lý sự kiện khi bấm nút gửi mã otp
        //Lấy số điện thoại và mã vùng
        String phonenumber = ccp.getDefaultCountryCodeWithPlus()+txtPhone.getText().toString().trim();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //khi verify thành công
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }
            //Verify thất bại
            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(VerifyPhoneActivity.this, getString(R.string.send_code_to_activate_fail), Toast.LENGTH_SHORT).show();
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

    //
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Xác thực OTP thành công
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Xác thực OTP thất bại
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //
                            }
                        }
                    }
                });
    }

    //Đi đến trang nhập mã otp
    private void GoToEnterCodeOtp(String verificationId, String phonenumber) {
        Intent intent = new Intent(this, EnterCodeOtpActivity.class);
        //truyền verificationid và phonenumber và EnterCodeActivity
        intent.putExtra("verificationId", verificationId);//truyền verificationId vào intent
        intent.putExtra("phonenumber", phonenumber);//truyền số vào intent
        startActivity(intent);
    }
}