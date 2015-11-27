package com.example.henrique.cerberus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends ActionBarActivity {

    public static final String IP_SERVER = "http://192.168.1.109/cerberus/";
    //public static final String IP_SERVER = "https://cerberus-thiagolira1.c9users.io/";

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

        passwd = CryptWith.cryptWith(passwd);

        RetrieveData retrieveData = new RetrieveData();
        // PerformBackgroundTask this class is the class that extends AsynchTask
        retrieveData.execute();
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            try {
                URL server = new URL(IP_SERVER + "get_login.php?login_java=" + login +
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
            String id_rasp = jsonObject.getString("id_rasp");

            Intent lock = new Intent(MainActivity.this, LockCar.class);
            lock.putExtra("id_rasp", id_rasp);
            startActivity(lock);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Senha ou login incorretos", Toast.LENGTH_LONG).show();
        }
    }

    public static class CryptWith {

        public static String cryptWith(String pass) throws NoSuchAlgorithmException {
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
