package com.android.zalochat.model;

import java.util.Date;
import java.util.List;

public class Chat {
    protected String id;
    protected List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    //protected String receiver;
    protected long lasttime;
    protected String lastmessage;

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
