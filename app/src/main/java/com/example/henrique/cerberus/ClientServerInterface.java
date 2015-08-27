package com.example.henrique.cerberus;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by henrique on 26/08/15.
 */

//Helps get data from server
public class ClientServerInterface {
    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";

    //constructor
    public ClientServerInterface(){}

    public JSONObject makeHttpRequest(String url){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
    }
}
