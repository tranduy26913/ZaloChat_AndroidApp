package com.android.zalochat.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.zalochat.R;
import com.android.zalochat.adapter.UserChatAdapter;
import com.android.zalochat.event.IClickItemUserChatListener;
import com.android.zalochat.mapping.UserMapping;
import com.android.zalochat.model.Chat;
import com.android.zalochat.model.User;
import com.android.zalochat.model.payload.UserChat;
import com.android.zalochat.util.Constants;
import com.android.zalochat.view.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerViewUserChat;//Li??n k???t v???i RecyclerView s??? ??i???n tho???i
    private User userOwn;//Bi???n l??u th??ng tin t??i kho???n c???a m??nh
    private FirebaseFirestore database;//BI???n l??u li??n k???t v???i database
    private UserChatAdapter userChatAdapter;//Userchat Adapter
    private List<UserChat> userChatList = new ArrayList<>();//Danh s??ch c??c t??i kho???n chat
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseFirestore.getInstance();//G???n instance v??o bi???n database
        recyclerViewUserChat = view.findViewById(R.id.recyclerViewUserChat);//Li??n k???t layout Recycler view Userchat v???i bi???n
        SharedPreferences ref = view.getContext().getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Khai b??o Shared Preferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//L???y th??ng tin t??i kho???n c???a m??nh t??? Shared Preferences
        try {
            Gson gson  = new Gson();//Khai b??o 1 Gson ????? l??m vi???c v???i Json
            userOwn = gson.fromJson(jsonUser,User.class);//Chuy???n th??ng tin t??? json sang object User ????? s??? d???ng
        }catch (Exception ex) {
            //GotoLogin();
        }
        LoadUserChat();//Load ra danh s??ch User Chat
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    private void LoadUserChat(){
        //Collection CHATS c?? nhi???m v??? ????nh d???u 2 t??i kho???n c?? nh???n tin v???i nhau
        //Ch??? khi 2 t??i kho???n c?? nh???n tin v???i nhau m???i t???o th??nh 1 document Chat

        // truy v???n v??o nh??nh id c???a Collection Chat ????? t??m ra danh s??ch nh???ng ng?????i c?? nh???n tin v???i m??nh
        CollectionReference chatRef = database.collection(Constants.CHAT_COLLECTION);
        chatRef.whereArrayContains("users",userOwn.getUserId())
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userChatList.clear();//Xo?? danh s??ch Userchat c??
                for(QueryDocumentSnapshot doc:value){//Duy???t qua t???ng document tho??? ??i???u ki???n ??? tr??n
                    Chat chat = doc.toObject(Chat.class);//Mapping t??? Document sang object Chat
                    String idFriend ="";//l??u id c???a t??i kho???n kh??c

                        for (String id:chat.getUsers()) {//duy???t qua danh s??ch user id trong object Chat
                            if(!id.equals(userOwn.getUserId())){//N???u kh??ng ph???i t??i kho???n c???a m??nh
                                idFriend = id;//G???n id cho idFriend
                            }
                        }

                    Task<DocumentSnapshot> user = database.collection(Constants.USER_COLLECTION)
                            .document(idFriend)//T??m ki???m th??ng tin user c?? id = idFriend trong database
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().exists()){
                                            //Mapping t??? User sang UserChat r???i add v??o danh s??ch Userchat
                                            userChatList.add(UserMapping.EntityToUserchat(task.getResult().toObject(User.class)
                                                    ,chat.getLastmessage(),chat.getId()));
                                            //g???i h??m notify ????? th??ng b??o cho adapter bi???t d??? li???u c?? s??? thay ?????i
                                            userChatAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                }

            }
        });
//Kh???i t???o m??t User Chat Adapter
        userChatAdapter = new UserChatAdapter(userChatList, new IClickItemUserChatListener() {
            //H??m c?? nhi???m v??? ?????nh ngh??a s??? ki???n khi click v??o item trong user chat adapter
            @Override
            public void onClickItemUserChat(UserChat userChat) {
                GoToChatActivity(userChat);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());//Khai b??o m???t LinearLayoutManager
        recyclerViewUserChat.setAdapter(userChatAdapter);//Set adapter cho recycler View
        recyclerViewUserChat.setLayoutManager(linearLayoutManager);//Set linearlayout cho recycler view nh???m ?????nh ngh??a layout khi hi???n th???
        recyclerViewUserChat.setHasFixedSize(true);//Set k??ch th?????c c?? th??? ch???nh ???????c
    }

    private void GoToChatActivity(UserChat userChat) {//M??? Chat activity
        Intent intent = new Intent(this.getContext(), ChatActivity.class);
        intent.putExtra(Constants.USER_JSON,userChat);//Truy???n th??ng tin UserChat qua Chat Activity
        startActivity(intent);
    }
}