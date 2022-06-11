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
    private RecyclerView recyclerViewPhoneBook;
    protected List<Contact> contactList;
    ContactAdapter adapter;
    User userOwn;
    public static final int REQUEST_READ_CONTACT = 102;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    ;
    CollectionReference userRef = database.collection(Constants.USER_COLLECTION);


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
        Log.e("GRANT RESULT:", "AAAA", null);
        recyclerViewPhoneBook = view.findViewById(R.id.RecycleViewListUserPhoneBook);
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(getContext(), contactList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewPhoneBook.setAdapter(adapter);
        recyclerViewPhoneBook.setLayoutManager(linearLayoutManager);
        recyclerViewPhoneBook.setHasFixedSize(true);
        if (!checkPermission(READ_CONTACTS, REQUEST_READ_CONTACT)) {
            requestPermissions1();
        } else {
            LoadData();
        }
    }

    private void LoadDataFromContact() {
        if (contactList.isEmpty()) {
            Uri uriContact = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                cursor = getActivity().getContentResolver().query(uriContact, null, null, null);
            } else {
                return;
            }

            while (cursor.moveToNext()) {
                String idName = ContactsContract.Contacts.DISPLAY_NAME;
                String idPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
                int colNameIndex = cursor.getColumnIndex(idName);
                int colPhoneIndex = cursor.getColumnIndex(idPhone);
                String name = cursor.getString(colNameIndex);
                String phone = cursor.getString(colPhoneIndex);
                if (phone.equals(userOwn.getPhone())) {
                    continue;
                }
                Contact newContact = new Contact();
                newContact.setPhone(phone);
                newContact.setFullname(name);
                contactList.add(newContact);
            }

            for (Contact newContact : contactList) {
                userRef.whereEqualTo("phone", newContact.getPhone()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (!newContact.getPhone().equals(userOwn.getUserId())) {//Loại bỏ trường hợp tự tìm bản thân
                                    User oldUser = doc.toObject(User.class);
                                    newContact.setAvatar(oldUser.getAvatar());
                                    newContact.setFullname(oldUser.getFullname());
                                    newContact.setUser(true);
                                    newContact.setOnline(oldUser.isOnline());
                                    newContact.setActive(oldUser.isActive());
                                    newContact.setUserId(oldUser.getUserId());
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }


    public boolean checkPermission(String permission, int requestCode) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions1() {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == REQUEST_READ_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoadData(); //call your dependent logic
            }
        }
    }
}