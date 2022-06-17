package com.android.zalochat.model;

import java.util.Date;
import java.util.List;

public class Chat { // Đối tượng đại diện cho đoạn chat giữa 2 người
    protected String id; // id của đoạn chat
    protected List<String> users; // id của 2 người chat của đoạn chat

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    //protected String receiver;
    protected long lasttime; // thời gian chat cuối cùng
    protected String lastmessage; // đoạn chat cuối cùng

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public Chat(String id,List<String> users, long lasttime, String lastmessage) {
        this.id = id;
        this.users=users;
        this.lastmessage = lastmessage;
        this.lasttime = lasttime;
    }
}
