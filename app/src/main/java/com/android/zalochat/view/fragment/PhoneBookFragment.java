package com.android.zalochat.view.fragment;

import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.adapter.ContactAdapter;
import com.android.zalochat.adapter.UserChatAdapter;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.model.Contact;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.util.ObjectSerializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PhoneBookFragment extends Fragment {
    private RecyclerView recyclerViewPhoneBook;
    protected List<Contact> contactList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();;
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
        recyclerViewPhoneBook = view.findViewById(R.id.RecycleViewListUserPhoneBook);
        sharedPreferences = getActivity().getSharedPreferences("dataZaloApp", getActivity().MODE_PRIVATE);
        editor= sharedPreferences.edit();
        try {
            contactList = (ArrayList<Contact>) ObjectSerializer.deserialize(sharedPreferences.getString("contactData", ObjectSerializer.serialize(new ArrayList<Contact>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("SIZE", "START LOADING DATA FROM CONTACT",null);
        LoadDataFromContact();
        Log.e("SIZE", "LoadDataFromContact after: "+contactList.size(),null);
        LoadDataToAdapter();
    }

    private void LoadDataFromContact() {
        if(contactList.isEmpty()){
            Uri uriContact = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                cursor = getActivity().getContentResolver().query(uriContact, null, null, null);
            }
            else{
                return;
            }

            while(cursor.moveToNext()){
                String idName = ContactsContract.Contacts.DISPLAY_NAME;
                String idPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
                int colNameIndex = cursor.getColumnIndex(idName);
                int colPhoneIndex = cursor.getColumnIndex(idPhone);
                String name = cursor.getString(colNameIndex);
                String phone = cursor.getString(colPhoneIndex);
                Contact newContact = new Contact();
                newContact.setPhone(phone);
                newContact.setFullname(name);
                userRef.whereEqualTo("phone",newContact.getPhone()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                User oldUser = doc.toObject(User.class);
                                newContact.setAvatar(oldUser.getAvatar());
                                newContact.setFullname(oldUser.getFullname());
                            }
                        }
                    }
                });
                contactList.add(newContact);
                Log.e("SIZE", "LoadDataFromContact: "+contactList.size(),null);
            }

            try {
                editor.putString("contactData", ObjectSerializer.serialize((Serializable) contactList));
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void LoadDataToAdapter() {
        Log.e("SIZE", "START LOADING DATA FOR ADAPTER",null);
        ContactAdapter contactAdapter = new ContactAdapter(this.getContext(),contactList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerViewPhoneBook.setAdapter(contactAdapter);
        recyclerViewPhoneBook.setLayoutManager(linearLayoutManager);
        recyclerViewPhoneBook.setHasFixedSize(true);
    }
}