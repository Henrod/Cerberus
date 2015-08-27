package com.example.henrique.cerberus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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

    //returns JSON object
    public JSONObject makeHttpRequest(String url){
        //http client helps to send and receive data
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //our requet method is post
        HttpPost httpPost = new HttpPost(url);

        try{
            //get response
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            //get the content and store it into inputstream object
            is = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                is.close();
                json = sb.toString();

                try {
                    jobj = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return jobj;
    }
}
