package com.android.zalochat.model;

import java.util.Date;

public class Message {
    protected String id;
    protected String sender;
    protected String receiver;
    protected String content;
    protected long time;
    protected String reaction;
    protected String type;

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

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
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

    public Message(String id, String sender, String receiver, String content, long time, String reaction, String type) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.reaction = reaction;
        this.time = time;
        this.type = type;


    }

}
