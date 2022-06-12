package com.android.zalochat.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

public class CheckUrlExist extends AsyncTask<String, Void, Boolean> {
    public boolean resultfromcheck;

    @Override
    protected Boolean doInBackground(String... strings) {
        int statusCode = 400;
        HttpURLConnection con;
        String urlString = strings[0].toString();
        try {
            con = (HttpURLConnection) new URL(urlString).openConnection();
            HttpURLConnection.setFollowRedirects(false);
            con.setRequestMethod("GET");
            con.connect();
            statusCode = con.getResponseCode();
        } catch (IOException e) {
            Log.e("ERROR",e.getMessage());
        }
        resultfromcheck = statusCode == HttpURLConnection.HTTP_NOT_FOUND;
        Log.e("RESULT",String.valueOf(resultfromcheck));
        return (statusCode == HttpURLConnection.HTTP_NOT_FOUND);
    }

    protected void onPostExecute(Boolean result) {

    }
}
