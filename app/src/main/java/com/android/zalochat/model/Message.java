package com.android.zalochat.model;

import java.util.Date;

public class Message { // Đối tượng cho đoạn tin nhắn giữa 2 người
    protected String id; // id của đoạn tin nhắn
    protected String sender; // id của người gửi
    protected String receiver; // id của người nhận
    protected String content; // nội dung đoạn chat
    protected long time; // thời gian gửi
    protected int reaction; // thông số reaction
    protected String type; // dạng tin nhắn ( HÌNH ẢNH, GHI ÂM, TEXT)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message() {

    }

    public Message(String id, String sender, String receiver, String content, long time, int reaction, String type) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.reaction = reaction;
        this.time = time;
        this.type = type;
    }

}
