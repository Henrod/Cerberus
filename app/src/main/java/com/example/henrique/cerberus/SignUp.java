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
    EditText et_login, et_passwd, et_passwd_confirm, et_id_rasp;
    String login, passwd, passwd_confirm, id_rasp;
    boolean msg = false, login_exist = false, exist_rasp = true, free_rasp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_login            = (EditText) findViewById(R.id.et_login_sign);
        et_passwd           = (EditText) findViewById(R.id.et_passwd_sign);
        et_passwd_confirm   = (EditText) findViewById(R.id.et_passwd_confirm);
        et_id_rasp          = (EditText) findViewById(R.id.et_rasp_id);
    }

    public void new_register(View view) throws NoSuchAlgorithmException {
        id_rasp = et_id_rasp.getText().toString();
        login = et_login.getText().toString();
        passwd = et_passwd.getText().toString();
        passwd_confirm = et_passwd_confirm.getText().toString();

        if(passwd.equals(passwd_confirm)){
            findViewById(R.id.ok_button).setClickable(false);
            passwd = MainActivity.CryptWith.cryptWith(passwd);
            (new RetrieveData()).doInBackground();
        } else {
            Toast.makeText(SignUp.this, "Senhas não correspondentes", Toast.LENGTH_LONG).show();
        }
    }

    class RetrieveData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //establish server socket

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    BufferedReader in;
                    try {
                        URL server = new URL(MainActivity.ip_server + "set_login.php?login_java=" + login +
                                "&senha_java=" + passwd + "&id_java=" + id_rasp);
                        in = new BufferedReader(new InputStreamReader(server.openStream()));
                        String message = in.readLine();
                        Log.i("web message", message);
                        switch (message) {
                            case "Login existente":
                                login_exist = true;
                            case "Dispositivo em uso":
                                free_rasp = false;
                            case "ID inexistente":
                                exist_rasp = false;
                                break;
                            default:
                                msg = true;
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                break;
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
                            while (login_exist || !free_rasp || !exist_rasp) {
                                if(login_exist) {
                                    login_exist = false;
                                    Toast.makeText(SignUp.this, "Login já existente. Por favor escolha outro.", Toast.LENGTH_LONG).show();
                                    findViewById(R.id.ok_button).setClickable(true);
                                } else if(!free_rasp) {
                                    free_rasp    = true;
                                    Toast.makeText(SignUp.this, "Dispositivo já em uso. Adicione o correto ID.", Toast.LENGTH_LONG).show();
                                    findViewById(R.id.ok_button).setClickable(true);
                                } else if (!exist_rasp) {
                                    exist_rasp = true;
                                    Toast.makeText(SignUp.this, "Dispositivo inexistente. Adicione o correto ID.", Toast.LENGTH_LONG).show();
                                    findViewById(R.id.ok_button).setClickable(true);
                                }
                            }
                        }
                    });
                }
            }.start();

            return null;
        }
    }

}