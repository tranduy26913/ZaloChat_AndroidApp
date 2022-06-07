package com.android.zalochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();//lấy instance kết nối tới database firestore
    private RecyclerView rvSearchUser;//Liên kết tới phần tử RecyclerView
    private SearchView searchViewSearchUser;//Liên kết tới phần tử SearchView
    private User userOwn;//Biến lưu thông tin tài khoản của mình
    private List<UserChat> userChatList = new ArrayList<>();//Danh sách kết quả tìm kiếm
    private SearchAdapter adapter;//Khai báo 1 SearchAdapter
    private String query;//Biến lưu giá trị tìm kiếm mà người dùng nhập vào
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);//thiết lập layout activity_search

        SharedPreferences ref = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Khai báo SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy json của user từ sharedPreferences
        try {
            Gson gson  = new Gson();//Gson thực hiện các xử lý liên quan đến json
            userOwn = gson.fromJson(jsonUser,User.class);//CHuyển từ json sang object User
        }catch (Exception ex) {
            //GotoLogin();
        }
        SetControl();//Thiết lập các control từ giao diện
        SetEvent();//Thiết lập các sự kiện cho control

    }

    //Thiết lập các sự kiện cho từng control
    private void SetEvent() {
        //Thiết lập sự kiện khi gõ vào thanh tìm kiếm
        searchViewSearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = searchViewSearchUser.getQuery().toString();//lấy nội dung tìm kiếm của người dùng nhập vào
                if(query.length()<3){//trường hợp người dùng nhập ít hơn 3 kí tự thì ko tìm kiếm
                    return false;
                }
                if(query.getBytes()[0]=='0'){//Nếu người dùng nhập có số 0 ở đầu thì thay bằng +84
                    query ="+84"+ query.substring(1);
                }
                //Truy vấn trong collection USERS
                database.collection(Constants.USER_COLLECTION)
                        .whereGreaterThanOrEqualTo("phone",query)//điều kiện là có chứa số điện thoại do người dùng nhập
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                userChatList.clear();
                                for (QueryDocumentSnapshot doc:value) {//duyệt qua từng document kết quả
                                    User user = doc.toObject(User.class);//Mapping từ document sang object User
                                    if(!user.getUserId().equals(userOwn.getUserId())){//Loại bỏ trường hợp tự tìm bản thân
                                            userChatList.add(UserMapping.EntityToUserchat(user,"",""));//thêm vào danh sách tìm kiếm
                                    }
                                }

                                adapter = new SearchAdapter(getApplicationContext(),userChatList);//khởi tạo 1 adapter Search
                                adapter.notifyDataSetChanged();//thông báo cho adapter là dữ liệu có thay đổi để load lại danh sách
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());//Khai báo 1 linearlayout
                                rvSearchUser.setAdapter(adapter);//gắn adapter cho recycler view Search
                                rvSearchUser.setLayoutManager(linearLayoutManager);//gắn linear layout cho recycler view
                                rvSearchUser.setHasFixedSize(true);//cho phép recyclerview tự tối ưu kích thước hiển thị
                            }
                        });


                return false;
            }
        });
    }

    //Thiết lập các control
    private void SetControl(){
        this.rvSearchUser = findViewById(R.id.recyclerViewSearchUser);//Gắn layout recyclerViewSearchUser cho biến
        this.searchViewSearchUser =findViewById(R.id.searchViewSearchUser);//Gắn layout searchViewSearchUser cho biến
    }

}