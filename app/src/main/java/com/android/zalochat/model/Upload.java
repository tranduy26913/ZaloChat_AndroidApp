package com.android.zalochat.model;

import java.io.Serializable;

public class Upload implements Serializable {
    protected String url;
    protected String userId;
    protected Long date;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
