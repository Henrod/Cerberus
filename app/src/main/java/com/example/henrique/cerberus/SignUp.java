package com.example.henrique.cerberus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by henrique on 19/09/15.
 */
public class SignUp extends Activity {
    EditText et_login, et_passwd, et_passwd_confirm;
    String login, passwd, passwd_confirm;
    boolean msg = false, login_exist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_login = (EditText) findViewById(R.id.et_login_sign);
        et_passwd = (EditText) findViewById(R.id.et_passwd_sign);
        et_passwd_confirm = (EditText) findViewById(R.id.et_passwd_confirm);
    }

    public void new_register(View view) throws NoSuchAlgorithmException {
        login = et_login.getText().toString();
        passwd = et_passwd.getText().toString();
        passwd_confirm = et_passwd_confirm.getText().toString();

        if(passwd.equals(passwd_confirm)){
            findViewById(R.id.ok_button).setClickable(false);
            passwd = MainActivity.CryptWithMD5.cryptWithMD5(passwd);
            (new RetrieveData()).doInBackground();
        } else {
            Toast.makeText(SignUp.this, "Senhas não correspondentes", Toast.LENGTH_LONG).show();
        }
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socker

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    BufferedReader in;
                    try {
                        URL server = new URL(MainActivity.ip_server + "set_login.php?login_java=" + login +
                                "&senha_java=" + passwd);
                        in = new BufferedReader(new InputStreamReader(server.openStream()));
                        if(in.readLine().equals("Login já existente")) {
                            login_exist = true;
                        } else {
                            msg = true;
                            startActivity(new Intent(SignUp.this, MainActivity.class));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            while(msg) {
                                msg = false;
                                Toast.makeText(SignUp.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            while (login_exist) {
                                login_exist = false;
                                Toast.makeText(SignUp.this, "Login já existente. Por favor escolha outro.", Toast.LENGTH_LONG).show();
                                findViewById(R.id.ok_button).setClickable(true);
                            }
                        }
                    });
                }
            }.start();

            return null;
        }
    }

}