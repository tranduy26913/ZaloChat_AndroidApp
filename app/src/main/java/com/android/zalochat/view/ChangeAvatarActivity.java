package com.android.zalochat.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.zalochat.R;
import com.android.zalochat.model.User;
import com.android.zalochat.util.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ChangeAvatarActivity extends AppCompatActivity {
    private ImageView imageView;//Liên kết đến phần tử ảnh đại diện bị thay đổi
    private Button btnUploadImage;//Liên kết đến phần tử nút tải ảnh lên firebase và update
    private Button btnChooseImage;//Liên kết đến phần tử nút chọn ảnh từ gallery
    private User userOwn; //User hiện tại đang đăng nhập
    private Uri imageUri;//Uri hình ảnh đã tại lên
    private FirebaseStorage firebaseStorage;//FirebaseStorage của firebase
    private StorageReference storageReference;//StorageReference của firebase
    private FirebaseFirestore database;//FirebaseFirestore của firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_avatar);// hiển thị activity activity_account_setting
        setView();// Set các element với biến
        setEvent();//Cài đặt event cho element
        SharedPreferences ref = this.getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE);//Lấy ra vùng nhớ SharedPreferences
        String jsonUser = ref.getString(Constants.USER_JSON, "");//lấy ra User được lưu ở SharedPreferences hiện tại
        try {
            Gson gson  = new Gson();
            userOwn = gson.fromJson(jsonUser,User.class);//convert json sang User
        }catch (Exception ex) {
            //GotoLogin();
        }
        Picasso.get().load(userOwn.getAvatar()).into(this.imageView);// Set ảnh cho view Image
    }

    private void setView() {// set biến ứng với layout ở view
        this.btnUploadImage = findViewById(R.id.btnUpdateImage);// set biến ứng với nút tải ảnh lên
        this.btnChooseImage = findViewById(R.id.btnChooseImage);// set biến ứng với nút chọn ảnh ở gallery
        this.imageView = findViewById(R.id.imageViewChangeAvatar);// set biến ứng với ảnh bị chọn
        this.firebaseStorage = FirebaseStorage.getInstance();//lấy instance từ firebase
        this.database = FirebaseFirestore.getInstance();//lấy instance từ firebase
        this.storageReference = firebaseStorage.getReference();//lấy instance từ firebase
    }

    private void setEvent() {
        this.btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }//Event của btnChooseImage
        });

        this.btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadPicture();//Event của btnUploadImage
            }
        });


    }

    private void choosePicture(){
        Intent intent=new Intent();// tạo 1 intent mới
        intent.setType("image/*"); // Mở gallery image
        intent.setAction(Intent.ACTION_GET_CONTENT);//hoạt động trả về
        startActivityForResult(intent,1);//trả về intent và code
    }

    @Override//hoạt động khi có activity khác trả về cụ thể là choosePicture ở trên
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null && data.getData()!=null){//Kiểm tra kết quả trả về đúng ko
            imageUri = data.getData();//lấy ra uri và gán vào biến global
            this.imageView.setImageURI(imageUri);// gắn ảnh và image view ở trên

        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);// Thanh hiển thị tiến độ tải ảnh lên
        pd.setTitle("Đang tải ảnh lên");//Tiêu đề của dialog trên
        pd.show();//Cho nó hiển thị
        final String randomKey = UUID.randomUUID().toString();//Tạo tên image tải lên cho ddwox trùng
        StorageReference riversRef = storageReference.child("images/"+randomKey);//Nơi lưu ảnh+ tên

        riversRef.putFile(imageUri)// bắt đầu tải ảnh lên
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Thành công thì chạy vào đây
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(R.id.changeAvatar_content),"Đã tải ảnh xong",Snackbar.LENGTH_LONG).show();//Tạo snack bar thông báo thành công
                        pd.dismiss();//tắt dialog tiến độ
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();//lấy kết quả uri từ file vừa tải lên
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {//lấy kết quả thành công thi chạy vào đây
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();//lấy ra url vừa nãy
                                UploadUrl(imageUrl);//Update vào user hiện tại
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {//Thất bại thì vào đây
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();//tắt dialog tiến độ
                        Toast.makeText(getApplicationContext(),"Tải ảnh thất bại",Toast.LENGTH_SHORT);//tạo toast thông báo thất bại
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override//Trong khi tải lên thì này sẽ hoạt đôngkj
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());//Lấy ra tỉ lệ phần trăm tiến độ bằng cách lấy số byte đã tải lên chia cho tổng số byte cân tải
                        pd.setMessage("Progress: "+(int)progress+"%");//Hiển thị ra số % lien tục
                    }
                });
    }
    public void UploadUrl(String url){
        SharedPreferences.Editor prefedit
                = getSharedPreferences(Constants.SHAREPREF_USER, MODE_PRIVATE).edit();// Mở trình chỉnh sửa SharedPreferences
        userOwn.setAvatar(url); //gắn url avatar đã tải lên vào user
        Gson gson = new Gson();
        String jsonUser = gson.toJson(userOwn); // Convert sang json
        prefedit.putString(Constants.USER_JSON, jsonUser); // gắn user lại vào vùng nhớSharedPreferences
        prefedit.apply();//Xác nhận thay đổi
        this.database.collection(Constants.USER_COLLECTION).document(userOwn.getUserId()).update("avatar",userOwn.getAvatar())//cập nhật lại user vừa chỉnh sửa
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {//Thành công thì chạy vào đây
                        Toast.makeText(ChangeAvatarActivity.this, "Đổi avatar thành công", Toast.LENGTH_SHORT).show();//Toast thông báo thành công
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {//Thất bại chạy vào đây
                Toast.makeText(ChangeAvatarActivity.this, "Có lỗi trong việc cập nhật avatar ở database", Toast.LENGTH_SHORT).show();//Toast thông báo thất bại
            }
        });
    }
}