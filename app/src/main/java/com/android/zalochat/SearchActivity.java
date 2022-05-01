package com.android.zalochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.zalochat.adapter.SearchAdapter;
import com.android.zalochat.adapter.UserChatAdapter;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.mapping.UserMapping;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView rvSearchUser;
    SearchView searchViewSearchUser;
    User userOwn;
    List<UserChat> userChatList = new ArrayList<>();
    private SearchAdapter adapter;
    private String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SharedPreferences ref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);
        String jsonUser = ref.getString(Constants.USER_JSON, "");
        try {
            Gson gson  = new Gson();
            userOwn = gson.fromJson(jsonUser,User.class);
        }catch (Exception ex) {
            //GotoLogin();
        }SetControl();
        SetEvent();

    }

    private void SetEvent() {
        searchViewSearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = searchViewSearchUser.getQuery().toString();
                if(query.length()<3){
                    return false;
                }
                if(query.getBytes()[0]=='0'){
                    query ="+84"+ query.substring(1);
                }

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                // truy vấn vào nhánh username mà người dùng nhập
                DatabaseReference users = firebaseDatabase.getReference("USERS");

                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, User>> objectsGTypeInd =
                                new GenericTypeIndicator<HashMap<String, User>>() {
                                };
                        Map<String, User> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                        final List<User> objectArrayList = new ArrayList<>(objectHashMap.values());
                        userChatList.clear();
                        for (User user: objectArrayList ) {
                            if(!user.getUserId().equals(userOwn.getUserId())){
                                if(user.getUserId().contains(query)){
                                    userChatList.add(UserMapping.EntityToUserchat(user,""));
                                }
                            }
                        }

                        adapter = new SearchAdapter(getApplicationContext(),userChatList);
                        adapter.notifyDataSetChanged();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rvSearchUser.setAdapter(adapter);
                        rvSearchUser.setLayoutManager(linearLayoutManager);
                        rvSearchUser.setHasFixedSize(true);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return false;
            }
        });
    }

    private void SetControl(){
        this.rvSearchUser = findViewById(R.id.recyclerViewSearchUser);
        this.searchViewSearchUser =findViewById(R.id.searchViewSearchUser);
    }

    private void LoadUserChat(){


    }
}