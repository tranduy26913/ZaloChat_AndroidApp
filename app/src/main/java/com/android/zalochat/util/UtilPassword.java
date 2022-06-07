package com.android.zalochat.util;

import org.mindrot.jbcrypt.BCrypt;

public class UtilPassword {//Util xử lý mã hoá và xác thực mật khẩu
    //Hàm mã hoá mật khẩu
    public static final String HashPassword(String plaintext){
        //Sử dụng BCrypt để mã hoá mật khẩu với salt 12
        String hash = BCrypt.hashpw(plaintext,BCrypt.gensalt(12));
        return hash;
    }
    //Hàm xác thực mật khẩu đã mã hoá
    public static final boolean verifyPassword(String password, String hash){
        return BCrypt.checkpw(password,hash);
    }
}
