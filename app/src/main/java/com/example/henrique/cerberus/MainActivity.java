package com.example.henrique.cerberus;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    JSONObject jobj = null;
    ClientServerInterface clientServerInterface = new ClientServerInterface();
    TextView textView;
    String ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textview);

        //start background process
        new RetrieveData().execute();
    }

    class RetrieveData extends AsyncTask <String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                jobj = clientServerInterface.makeHttpRequest("http://192.168.1.109/cerberus/main.php");
                ab = jobj.getString("key");
                Log.d("http", "passei aqui ab = " + ab);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return ab;
        }

        protected void onPostExecute(String ab){
            if(ab != null)
                textView.setText(ab);
            else
                textView.setText("Não funfou");
        }
    }
}
