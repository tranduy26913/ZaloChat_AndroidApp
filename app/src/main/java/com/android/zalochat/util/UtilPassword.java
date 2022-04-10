package com.android.zalochat.util;

import org.mindrot.jbcrypt.BCrypt;

public class UtilPassword {
    public static final String HashPassword(String plaintext){
        String hash = BCrypt.hashpw(plaintext,BCrypt.gensalt(12));
        return hash;
    }
    public static final boolean verifyPassword(String password, String hash){
        return BCrypt.checkpw(password,hash);
    }
}
