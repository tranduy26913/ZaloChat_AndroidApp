package com.android.zalochat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.UtilPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    Button btnRegister;
    EditText txtPhone,txtPassword,txtName;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = findViewById(R.id.btnRegister);
        txtName = findViewById(R.id.txtNameRegister);
        txtPhone = findViewById(R.id.txtPhoneRegister);
        txtPassword =findViewById(R.id.txtPasswordRegister);
        ccp = findViewById(R.id.ccp);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = ccp.getDefaultCountryCodeWithPlus()+  txtPhone.getText().toString().trim();
                String password = txtPassword.getText().toString();
                String fullname = txtName.getText().toString();
                User user = new User(phone,password,fullname);
                if(validate(user)){
                    user.setPassword(UtilPassword.HashPassword(password));
                    RegisterAccount(user);
                }
            }
        });
    }

    private void RegisterAccount(User user){

        database.collection(Constants.USER_COLLECTION)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,
                                    "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();

                            GoToVerifyPhoneNumber();
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this,
                                    "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void GoToVerifyPhoneNumber() {
        Intent intent = new Intent(this,VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", txtPhone.getText().toString().trim());
        startActivity(intent);
    }

    public void onClickGoToLogin(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private boolean validate(User user){
        String VIETNAMESE_DIACRITIC_CHARACTERS
                = "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ";

        Pattern numphone = Pattern.compile("\\+\\d+$");

        boolean rs = true;
        if(user.getPhone().startsWith("+840")){
            Toast.makeText(this,"Số điện thoại không được bắt đầu với 0",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(!user.getPhone().matches(numphone.pattern())){
            Toast.makeText(this,"Số điện không được tồn tại kí tự ngoài số",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getPhone() == null ||user.getPhone().length() != 12){
            Toast.makeText(this,"số điện thoại bao gồm 9 chữ số",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getFullname() == null || user.getFullname().length() == 0 || user.getFullname().length() > 50){
            Toast.makeText(this,"Tên không hợp lệ",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getPassword() == null || user.getPassword().length() == 0|| user.getPassword().length() > 50){
            Toast.makeText(this,"Mật khẩu không hợp lệ",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        return rs;

    }
}