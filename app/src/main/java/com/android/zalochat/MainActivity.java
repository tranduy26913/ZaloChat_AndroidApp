package com.android.zalochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.LoginActivity;
import com.android.zalochat.view.fragment.MessageFragment;
import com.android.zalochat.view.fragment.PhoneBookFragment;
import com.android.zalochat.view.fragment.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private SearchView searchViewMain;
    //new NavigationBarView.OnItemSelectedListener()
    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment;
        switch (item.getItemId()){
            case R.id.miMessage: {
                selectedFragment = new MessageFragment();
                break;
            }
            case R.id.miPhonebook: {
                selectedFragment = new PhoneBookFragment();
                break;
            }
            default:
                selectedFragment = new AccountFragment();
                break;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutMain,selectedFragment).commit();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckLogin();
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(navListener);
        bottomNavigation.setSelectedItemId(R.id.miMessage);
        searchViewMain =findViewById(R.id.searchViewMain);
        searchViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GoToSearch();
            }
        });
        searchViewMain.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToSearch();
            }
        });
    }

    private void CheckLogin() {
        SharedPreferences pref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);
        Gson gson =new Gson();
        String userOwnJson = pref.getString(Constants.USER_JSON, "");//lấy json user từ share pref
        if(userOwnJson.equals("")){
            GoToLogin();
        }
else {

            try {
                User userOwn = gson.fromJson(userOwnJson, User.class);//parse sang class User
            }catch (Exception ex){
                GoToLogin();
                Toast.makeText(this, "Phiên làm việc đã hết. Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void GoToLogin() {
        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);finish();
    }

    private void GoToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

}