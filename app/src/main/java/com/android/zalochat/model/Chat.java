package com.android.zalochat.model;

import java.util.Date;

public class Chat {
    protected String id;
    protected String sender;
    protected String receiver;
    protected long lasttime;
    protected String lastmessage;

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

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public Chat() {

    }


    public Chat(String id,String sender, String receiver, long lasttime, String lastmessage) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.lastmessage = lastmessage;
        this.lasttime = lasttime;
    }
}
