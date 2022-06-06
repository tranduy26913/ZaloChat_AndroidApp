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
    final FirebaseFirestore database = FirebaseFirestore.getInstance();//Khai báo firestore
    Button btnRegister;//Liên kết đến phần tử Button Register
    EditText txtPhone,txtPassword,txtName;//Liên kết đến các phần tử EditText số điện thoại, mật khẩu, tên đầy đủ
    CountryCodePicker ccp;//Liên kết đến phần tử Country Code picker chọn mã vùng sđt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//Thiết lập view layout activity_register
        btnRegister = findViewById(R.id.btnRegister);//Gắn Button Register cho biến
        txtName = findViewById(R.id.txtNameRegister);//Gắn EditText Tên đầy đủ cho biến
        txtPhone = findViewById(R.id.txtPhoneRegister);//Gắn EditText số điện thoại cho biến
        txtPassword =findViewById(R.id.txtPasswordRegister);//Gắn EditText mật khẩu cho biến
        ccp = findViewById(R.id.ccp);//Gắn Country Code Picker mã vùng sđt cho biến

        btnRegister.setOnClickListener(new View.OnClickListener() {//Thiết lập sự kiện khi bấm Button Register
            @Override
            public void onClick(View view) {
                //Lấy số điẹn thoại của người dùng nhập vào kèm theo mã vùng
                String phone = ccp.getDefaultCountryCodeWithPlus()+  txtPhone.getText().toString().trim();
                String password = txtPassword.getText().toString();//Lấy mật khẩu do ng dùng nhập
                String fullname = txtName.getText().toString();//Lấy tên đầy đủ
                User user = new User(phone,password,fullname);//Khởi tạo 1 object User
                if(validate(user)){//Nếu user là hợp lệ
                    user.setPassword(UtilPassword.HashPassword(password));//Hash mật khẩu và gắn lại mật khẩu đã mã hoá
                    RegisterAccount(user);//Đăng ký tài khoản, lưu vào database
                }
            }
        });
    }

    private void RegisterAccount(User user){//Đăng ký tài khoản

        database.collection(Constants.USER_COLLECTION)
                .document(user.getUserId())
                .set(user)//Tạo 1 document mới vào collection USERS
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {//Nếu thêm vào database thành công
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,
                                    "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();

                            GoToVerifyPhoneNumber();//Đi đến trang xác thực số điện thoại
                            finish();
                        }else {
                            Toast.makeText(RegisterActivity.this,
                                    "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void GoToVerifyPhoneNumber() {//đi đến Verify Phone Activity
        Intent intent = new Intent(this,VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", txtPhone.getText().toString().trim());//Truyền số điện thoại bằng intent qua Verify Phone Activity
        startActivity(intent);
    }

    public void onClickGoToLogin(View view) {//Xử lý sự kiện khi bấm vào nút đi đến Login
        //Chuyển từ Register Activity sang Login Activity
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private boolean validate(User user){//Xác thực tính hợp lệ của user
        String VIETNAMESE_DIACRITIC_CHARACTERS
                = "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ";

        Pattern numphone = Pattern.compile("\\+\\d+$");//Pattern kiểm tra số điện thoại có đúng form không

        boolean rs = true;
        if(user.getPhone().startsWith("+840")){//kiểm tra số 0 đầu tiên, vì dùng mã vùng nên ko có số 0
            Toast.makeText(this,"Số điện thoại không được bắt đầu với 0",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(!user.getPhone().matches(numphone.pattern())){//kiểm tra xem số điện thoại có kí tự khác số không
            Toast.makeText(this,"Số điện không được tồn tại kí tự ngoài số",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getPhone() == null ||user.getPhone().length() != 12){//kiểm tra độ dài số điện thoại
            Toast.makeText(this,"số điện thoại bao gồm 9 chữ số",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getFullname() == null || user.getFullname().length() == 0 || user.getFullname().length() > 50){
            //Kiểm tra xem tên đầy đủ có hợp lệ không, phải nhỏ hơn 50 kí tự
            Toast.makeText(this,"Tên không hợp lệ",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        else if(user.getPassword() == null || user.getPassword().length() == 0|| user.getPassword().length() > 50){
            //Kiểm tra tính hợp lệ của mật khẩu
            Toast.makeText(this,"Mật khẩu không hợp lệ",Toast.LENGTH_SHORT).show();
            rs = false;
        }
        return rs;

    }
}