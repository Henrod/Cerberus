package com.example.henrique.cerberus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.EncryptedPrivateKeyInfo;

public class MainActivity extends ActionBarActivity {

    public static final String ip_server = "http://192.168.1.111/cerberus/";

    EditText et_login;
    EditText et_passwd;
    String login, passwd, json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_login = (EditText) findViewById(R.id.et_login);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
    }

    public void start_login(View view) throws JSONException, NoSuchAlgorithmException {
        login = et_login.getText().toString();
        passwd = et_passwd.getText().toString();

        passwd = CryptWithMD5.cryptWithMD5(passwd);
        Log.d("crypt", passwd);

        RetrieveData retrieveData = new RetrieveData();
        // PerformBackgroundTask this class is the class that extends AsynchTask
        retrieveData.execute();
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL(ip_server + "get_login.php?login_java=" + login +
                                        "&senha_java=" + passwd);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.openStream()));
                json = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (json != null)
                        decode(json);
                    else
                        Toast.makeText(MainActivity.this, "Teste novamente mais tarde :(", Toast.LENGTH_LONG).show();

                }
            });

            return null;
        }
    }

    //decodes JSON, put ID to lock activity and start it
    private void decode(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int id = jsonObject.getInt("id");

            Intent lock = new Intent(MainActivity.this, LockCar.class);

            lock.putExtra("id", id);
            startActivity(lock);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Senha ou login incorretos", Toast.LENGTH_LONG).show();
        }
    }

    public static class CryptWithMD5 {

        public static String cryptWithMD5(String pass) throws NoSuchAlgorithmException {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (byte aDigested : digested) {
                sb.append(Integer.toHexString(0xff & aDigested));
            }
            return sb.toString();
        }
    }

    public void start_signup(View view) {
        startActivity(new Intent(MainActivity.this, SignUp.class));
    }

}
