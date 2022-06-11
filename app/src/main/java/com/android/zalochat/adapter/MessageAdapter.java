package com.android.zalochat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.zalochat.R;
import com.android.zalochat.model.payload.MessageChat;
import com.android.zalochat.util.Constants;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {//adapter hiển thị tin nhắn
    private List<MessageChat> messageList;//Danh sách các tin nhắn cần hiển thị
    private Context context;//Context chứa RecyclerView
    private FirebaseFirestore db = FirebaseFirestore.getInstance();//lấy instance liên kết tới database
    private String chatId;//Biến lưu chatId
    String pattern = "HH:mm";//Biến lưu pattern để hiển thị thời gian tin nhắn
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);//Khai báo Simple format để format thời gian

    MediaPlayer mPlayer= null;//Biến lưu mPlayer dùng để gán vào file âm thanh lấy từ firebase để phát
    URL url = null;//url của file âm thanh được lấy từ firebase

    final int MSG_SENDER = 0, MSG_RECEIVER = 1;//2 biến đánh dấu người gửi và người nhận

    public MessageAdapter(List<MessageChat> messageList, Context context, String chatId) {
        this.messageList = messageList;
        this.context = context;
        this.chatId = chatId;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_SENDER) {//Nếu là tin nhắn từ người gửi thì áp dụng layout item_message_sender
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sender, parent, false);
            return new MessageHolder(view);
        } else {//Nếu là tin nhắn từ người nhận thì áp dụng layout item_message_receiver
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receiver, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        MessageChat messageChat = messageList.get(position);//Lấy message theo vị trí position

        int reactions[] = new int[]{//Danh sách các reaction
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();//Khởi tạo 1 reaction config

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {//tạo 1 popup để chọn reaction

            if (pos < 0)//nếu không chọn reaction nào thì bỏ qua
                return false;
            if (pos == messageChat.getMessage().getReaction())//Nếu reaction mới chọn trùng với reaction cũ thì được xem là bỏ chọn
                pos = -1;//giá trị -1 được xem là ko có reaction nào
            if (pos == -1) {
                holder.imgReaction.setImageResource(R.drawable.ic_like_border);//Set hình ảnh like_border khi ko có reaction
            } else {
                holder.imgReaction.setImageResource(reactions[pos]);//set reaction theo đúng vị trí trong danh sách
            }

            messageChat.getMessage().setReaction(pos);//gắn lại giá trị reaction cho object message

            db.collection(Constants.MESSAGE_COLLECTION)
                    .document(chatId)
                    .collection(Constants.SUBMESSAGE_COLLECTION)
                    .document(messageChat.getMessage().getId()).update("reaction", pos);//cập nhập lại reaction lên database


            return true; // true is closing popup, false is requesting a new selection
        });


        holder.imgReaction.setOnTouchListener(new View.OnTouchListener() {//gắn sự kiện touch vào phần tử imgReaction
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popup.onTouch(view, motionEvent);
                return false;
            }
        });

        if (position > 0) {//xử lý trường hợp những tin nhắn liên tiếp sẽ chỉ hiện avatar 1 lần
            //Nếu các tin nhắn kế tiếp nhau là đều bên trái (người nhận) thì chỉ hiện avatar duy nhất 1 lần
            if (messageChat.getMessage().getReceiver().equals(messageList.get(position - 1).getMessage().getReceiver())) {

                holder.imgAvatarMessage.setVisibility(View.INVISIBLE);//ẩn avatar
            } else {
                holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());//hiện avatar
            }
        } else {//Nếu là tin nhắn đầu tiên thì luôn hiện avatar
            holder.imgAvatarMessage.setImageBitmap(messageChat.getAvatar());
        }
        Date date = new Date(messageChat.getMessage().getTime());//CHuyển thời gian từ long trong database sang date
        if (position < messageList.size()) {
            if (position == messageList.size() - 1) {
                //Nếu là tin nhắn cuối cùng thì luôn hiển thị thời gian
                holder.tvMessageTime.setText(simpleDateFormat.format(date));
            } else {
                if (!messageList.get(position).getMessage().getSender().equals(messageList.get(position + 1).getMessage().getSender())) {
                    //Nếu là tin nhắn cuối cùng trong 1 chuỗi các tin liên tiếp thì hiển thị thời gian
                    holder.tvMessageTime.setText(simpleDateFormat.format(date));
                } else {//các tin nhắn ở trên trong chuỗi tin nhắn sẽ ko hiển thị thời gian
                    holder.tvMessageTime.setVisibility(View.GONE);
                }
            }
        }

        if (messageChat.getMessage().getType().equals(Constants.IMAGE)) {//Trường hợp tin nhắn là hình ảnh
            holder.tvMessageContent.setVisibility(View.GONE);//ẩn đi tin nhắn văn bản
            holder.iconPlaySound.setVisibility(View.GONE);//ẩn đi icon phát âm thanh của tin nhắn
            holder.imgMessage.setVisibility(View.VISIBLE);//Hiển thị phần tử Image View để chứa hình ảnh
            holder.layoutMessageChatContent.setVisibility(View.GONE);//ẩn layout chứa tin nhắn văn bản
            Picasso.get().load(messageChat.getMessage().getContent()).into(holder.imgMessage);//Load hình ảnh từ  url rồi truyền vào cho imgMessage
            Picasso.get().load(messageChat.getMessage().getContent()).into(new Target() {//load hình ảnh từ url rồi truyền vào imgMessage
                //chia làm 3 trường hợp: đã load xong, load lỗi, chuẩn bị load
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //ở trường hợp load thành công thì hiển thị hình ảnh
                    holder.imgMessage.setVisibility(View.VISIBLE);//hiển thị layout imgMessage
                    holder.imgMessage.setImageBitmap(bitmap);//gán hình ảnh vào imgMessage
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    //trường hợp load thất bại do tuy có url nhưng ảnh không còn giá trị nên sẽ không hiển thị lên
                    //trường hợp load thất bại thì chỉ hiển thị textView thông báo ảnh xóa do quá hạn
                    holder.imgMessage.setVisibility(View.GONE);//ẩn đi widget hình ảnh trong tin nhắn
                    holder.layoutMessageChatContent.setVisibility(View.VISIBLE);//hiển thị khung chat của tin nhắn
                    holder.tvMessageContent.setVisibility(View.VISIBLE);//hiển thị nội dung văn bản của tin nhắn
                    holder.tvMessageContent.setText("Hình ảnh đã xoá do quá hạn");//các trường hợp load thất bại sẽ thông báo là ảnh xóa do quá hạn
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else if (messageChat.getMessage().getType().equals(Constants.SOUND)) {
            holder.imgMessage.setVisibility(View.GONE);// Ẩn đi phần tử Image View
            holder.iconPlaySound.setVisibility(View.VISIBLE);// hiển thị biểu tượng phát âm thanh
            holder.layoutMessageChatContent.setOnClickListener(v -> {//nếu tin nhắn là gửi âm thanh và ng dùng chọn thì sẽ chạy code dưới
                try {
                    if(holder.tvMessageContent.getText()=="....Đang phát...."){//nếu âm thanh đang phát thì sẽ dừng
                        mPlayer.stop();//dừng phát âm thanh
                        holder.tvMessageContent.setText("Âm thanh");//hiển thị text "Âm thanh" cho tin nhắn gửi âm thanh khi chưa phát hoặc dừng phát
                        url=null;//xóa hết giá trị url khi âm thanh dừng phát
                        mPlayer=null;//xóa hết giá trị của mPlayer khi âm thanh dừng phát, mPlayer!=null khi người dùng chọn tin nhắn phát âm thanh
                    }
                    else {
                        //gán giá trị mới để phát âm thanh của app
                        mPlayer=new MediaPlayer();
                        //lấy url của file âm thanh từ nội dung tin nhắn được gừi từ firebase
                        url = new URL(messageChat.getMessage().getContent());
                        //gán url vào mPlayer để có file âm thanh
                        mPlayer.setDataSource(String.valueOf(url));
                        // chuẩn bị file âm thanh
                        mPlayer.prepare();
                        // bấm vào phát ra âm thanh mà người dùng nhận được.
                        mPlayer.start();
                        //set Text thành trạng thái đang phát nhạc trên dt
                        holder.tvMessageContent.setText("....Đang phát....");
                    }
                } catch (MalformedURLException | ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            holder.tvMessageContent.setText("Âm thanh");//Gắn nội dung tin nhắn vào cho TextView Message content
        } else {//Trường hợp tin nhắn là văn bản
            holder.imgMessage.setVisibility(View.GONE);// Ẩn đi phần tử Image View
            holder.iconPlaySound.setVisibility(View.GONE);// Ẩn đi biểu tượng phát âm thanh của tin nhắn
            holder.tvMessageContent.setText(messageChat.getMessage().getContent());//Gắn nội dung tin nhắn vào cho TextView Message content
        }
        if (messageChat.getMessage().getReaction() != -1)//Nếu reaction = -1 thì hiển thị hình ảnh không có reaction là like_border
            holder.imgReaction.setImageResource(reactions[messageChat.getMessage().getReaction()]);


    }


    @Override
    public int getItemCount() {
        if (messageList == null)
            return 0;
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences ref = context.getSharedPreferences(Constants.SHAREPREF_USER, Context.MODE_PRIVATE);
        String userId = ref.getString(Constants.USERID, "");
        if (messageList.get(position).getMessage().getSender().equals(userId)) {
            return MSG_SENDER;
        } else return MSG_RECEIVER;
    }
}

