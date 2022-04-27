package com.android.zalochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.android.zalochat.view.fragment.MessageFragment;
import com.android.zalochat.view.fragment.PhoneBookFragment;
import com.android.zalochat.view.fragment.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
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
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(navListener);
        bottomNavigation.setSelectedItemId(R.id.miMessage);

    }

}