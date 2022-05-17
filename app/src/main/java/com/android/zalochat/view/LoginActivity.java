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

    EditText txtPhoneLogin, txtPasswordLogin;
    CountryCodePicker ccp;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin);
        txtPhoneLogin = findViewById(R.id.txtPhoneLogin);
        ccp = findViewById(R.id.ccp);
    }

    public void onClickLogin(View view) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Đang đăng nhập...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
// To dismiss the dialog

        String phone = ccp.getDefaultCountryCodeWithPlus() + txtPhoneLogin.getText().toString().trim();
        String password = txtPasswordLogin.getText().toString();

        database.collection(Constants.USER_COLLECTION)
                .document(phone)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        User user = task.getResult().toObject(User.class);
                        if (UtilPassword.verifyPassword(password, user.getPassword())) {
                            if (user.isActive()) {
                                SharedPreferences.Editor prefedit
                                        = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();
                                Gson gson = new Gson();
                                String jsonUser = gson.toJson(user);
                                prefedit.putString(Constants.USERID, user.getUserId());
                                prefedit.putString(Constants.USER_JSON, jsonUser);

                                prefedit.apply();
                                progress.dismiss();
                                GoToMainActivity();
                                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
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
            public void onCanceled() {
                Toast.makeText(LoginActivity.this, getString(R.string.login_false), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });


    }

    private void GoToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onTextViewSendMailClick(View view) {
        Intent intent = new Intent(this, VerifyPhoneActivity.class);
        intent.putExtra("phonenumber", txtPhoneLogin.getText().toString());
        startActivity(intent);
    }

    public void onClickGoToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}