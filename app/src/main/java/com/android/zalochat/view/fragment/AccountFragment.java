package com.android.zalochat.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.AccountSettingActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    private ConstraintLayout layoutInfoUser;//Layout nhấn vào để chuyển sang trang thông tin người dùng
    private TextView tvItemInfoUsern; //tên Hiển thị ở fragment
    private User userOwn;//Tài khoản đã đăng nhập
    private ImageView avatar;// Avatar người dùng
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // tạo view là fragment_Account
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        setView(view);//gắn các biến vào các element trên view
        setOnclick();// Gắn các sự kiện tương ứng với các element
        SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Lấy ra bộ nhớ SharedPreferences với đối tương là User
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy ra User dạng json
        try {
            Gson gson  = new Gson();
            userOwn = gson.fromJson(jsonUser,User.class);// Convert sang kiểu User
        }catch (Exception ex) {
            //GotoLogin();
        }
        this.tvItemInfoUsern.setText(userOwn.getFullname());//Đặt tên User lên element trên view
        Picasso.get().load(userOwn.getAvatar()).into(this.avatar);//Load ảnh đại diện cho user
        return view;

    }
    protected void setView(View view){
        layoutInfoUser = view.findViewById(R.id.layoutInfoUser);// Gắn layoutInfoUser cho biến
        tvItemInfoUsern = view.findViewById(R.id.tvItemInfoUsern);// gắn text tên cho biến
        avatar = view.findViewById(R.id.avatar_accout_fragment);// gắn image view cho biến

    }
    protected void setOnclick(){
        layoutInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);// Tạo itent với Account Setting Activity
                startActivity(intent);// Khởi chạy Intent
            }
        });

    }



}