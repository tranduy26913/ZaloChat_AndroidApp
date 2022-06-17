package com.android.zalochat.model;

import java.io.Serializable;

public class Upload implements Serializable { // Đối tượng này dùng để lưu trữ các thông tin của hình ảnh, ghi âm khi được upload lên hệ thống
    protected String url; // URL ở đây nghĩa là tên file theo quy tắc "thu-muc-luu-tru/ten-file"
    protected String userId; // Id người up
    protected Long date; // thời gian up
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
