package com.android.zalochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.android.zalochat.view.ChatActivity;
import com.android.zalochat.view.fragment.MessageFragment;
import com.android.zalochat.view.fragment.PhoneBookFragment;
import com.android.zalochat.view.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    selectedFragment = new ProfileFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutMain,selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(navListener);
        bottomNavigation.setSelectedItemId(R.id.miMessage);

    }

    public void chatPage(View view) {
        Intent intent=new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}