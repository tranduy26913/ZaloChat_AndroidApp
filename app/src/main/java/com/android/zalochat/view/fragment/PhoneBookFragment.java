package com.android.zalochat.view.fragment;

import static android.Manifest.permission.READ_CONTACTS;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.adapter.ContactAdapter;
import com.android.zalochat.model.Contact;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class PhoneBookFragment extends Fragment {
    private RecyclerView recyclerViewPhoneBook; //Dùng để liên kết với recycleview ở layout
    protected List<Contact> contactList; //Dùng để chứa thông tin danh bạ
    ContactAdapter adapter; // Dùng để liên kết item với recycleview
    User userOwn; // Người dùng hiện tại
    public static final int REQUEST_READ_CONTACT = 102; //mã request tự đặt cho việc xin quyền đọc danh bạ

    private FirebaseFirestore database = FirebaseFirestore.getInstance(); // Lấy đối tượng của firebase firestore

    CollectionReference userRef = database.collection(Constants.USER_COLLECTION); // Tạo một collectionreference ánh xạ tới collection trong database


    public PhoneBookFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewPhoneBook = view.findViewById(R.id.RecycleViewListUserPhoneBook); // Ánh xạ tới Recycleview ở layout
        contactList = new ArrayList<>(); // Tạo một arraylist rỗng
        adapter = new ContactAdapter(getContext(), contactList);  // Tạo adapter với context hiện tại và dữ liệu danh bạ
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()); // Tạo quản lý layout
        recyclerViewPhoneBook.setAdapter(adapter); // Gắn adapter vào reycycle view
        recyclerViewPhoneBook.setLayoutManager(linearLayoutManager); //Gắn quản lý layout vào recycleview
        recyclerViewPhoneBook.setHasFixedSize(true);
        if (!checkPermission(READ_CONTACTS, REQUEST_READ_CONTACT)) { //Kiểm tra xem ứng dụng có quyền đọc danh bạ hay chưa, dựa
            requestPermissions1(); //Xin quyền đọc danh bạ
        } else {
            LoadData(); // Lấy dữ liệu từ danh bạ và hiển thị ra màn hình
        }
    }

    private void LoadDataFromContact() {
        if (contactList.isEmpty()) { //kiểm tra xem danh sách danh bạ hiện tại có rỗng hay ko
            Uri uriContact = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //Truy cập vào dữ liệu danh bạ
            Cursor cursor; //Khai báo cursor để duyệt danh sách danh bạ từ uri
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { //Kiểm tra SDK có hỗ trợ việc truy vấn dữ liệu danh bạ qua cursor hay không
                cursor = getActivity().getContentResolver().query(uriContact, null, null, null); // Duyệt danh sách danh bạ từ thông qua cursor
            } else {
                return; // SDK không hỗ trợ thì không thực thi
            }

            while (cursor.moveToNext()) { // Duyệt từng dòng
                String idName = ContactsContract.Contacts.DISPLAY_NAME; // Lấy tên cột DISPLAY_NAME
                String idPhone = ContactsContract.CommonDataKinds.Phone.NUMBER; // Lấy tên cột NUMBER
                int colNameIndex = cursor.getColumnIndex(idName); // Dựa vào tên idName lấy ví trí cột
                int colPhoneIndex = cursor.getColumnIndex(idPhone); // Dựa vào tên idNumber lấy ví trị cột
                String name = cursor.getString(colNameIndex); // Lấy tên trong danh bạ ra
                String phone = cursor.getString(colPhoneIndex); // Lấy số điện thoại trong danh bạ ra
                if (phone.equals(userOwn.getPhone())) { // Nếu bị trùng số đt với người dùng đang đăng nhập thì skip
                    continue;
                }
                Contact newContact = new Contact(); // tạo contact mới
                newContact.setPhone(phone); // set số điện thoại trong danh bạ vô contact
                newContact.setFullname(name); // set tên trong danh bạ vô contact
                contactList.add(newContact); // thêm contact đó vào contact list
            }

            for (Contact newContact : contactList) { // Duyệt từng contact trong contactlist
                userRef.whereEqualTo("phone", newContact.getPhone()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // Tìm kiếm theo số điện thoại trong contact
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) { // Duyệt qua từng kết quả
                                if (!newContact.getPhone().equals(userOwn.getUserId())) {//Loại bỏ trường hợp tự tìm bản thân
                                    User oldUser = doc.toObject(User.class); // Ép kiểu về đối tượng user
                                    newContact.setAvatar(oldUser.getAvatar()); // Gắn thông tin của user vô contact
                                    newContact.setFullname(oldUser.getFullname());
                                    newContact.setUser(true);
                                    newContact.setOnline(oldUser.isOnline());
                                    newContact.setActive(oldUser.isActive());
                                    newContact.setUserId(oldUser.getUserId());
                                }
                            }
                        }
                        adapter.notifyDataSetChanged(); // Báo cho adapter biết là dữ liệu đã thay đổi để thay đổi UI
                    }
                });
            }
        }
    }


    public boolean checkPermission(String permission, int requestCode) { //Kiểm tra quyền của ứng dụng
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions1() { // Xin quyền truy cập danh bạ
        requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACT);
    }

    private void LoadData() {

        SharedPreferences ref = getActivity().getSharedPreferences(Constants.SHAREPREF_USER, getActivity().MODE_PRIVATE);//Khai báo SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//Lấy json của user từ sharedPreferences
        try {
            Gson gson = new Gson();//Gson thực hiện các xử lý liên quan đến json
            userOwn = gson.fromJson(jsonUser, User.class);//CHuyển từ json sang object User
        } catch (Exception ex) {

        }
        LoadDataFromContact();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { // Đây là một hàm có sẵn, dựa vào kết quả của việc xin quyền mà thực hiện
        // hành vi tiếp theo, ở đây lấy dữ liệu từ danh bạ và hiển thị ra màn hình. Lý do phải sử dụng như thế này để xử lý bất đồng bộ.
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_READ_CONTACT) { // Kiểm tra xem quyền vừa được xin có phải quyền đọc danh bạ hay ko = 102
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // So sánh kết quả xin quyền
                LoadData(); //lấy dữ liệu từ danh bạ và hiển thị ra màn hình
            }
        }
    }
}