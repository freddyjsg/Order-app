package com.example.elbaeat.ui.main;

import android.content.Intent;
import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.elbaeat.R;
import com.example.elbaeat.data.Result;
import com.example.elbaeat.data.model.LoggedInUser;
import com.example.elbaeat.ui.login.LoggedInUserView;
import com.example.elbaeat.ui.login.LoginActivity;

import com.example.elbaeat.ui.login.LoginViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ToggleButton PortoToggle;
    ToggleButton MarinaToggle;
    Button logoutButton;
    TextView UserTitle;
    public JSONObject jsonOrderInput = new JSONObject();
    public JSONObject jsonPushInput = new JSONObject();
    public JSONObject jsonDelPushInput = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PortoToggle = findViewById(R.id.toggleButton2);
        MarinaToggle = findViewById(R.id.toggleButton3);
        logoutButton = findViewById(R.id.LogoutButton);
        UserTitle = findViewById(R.id.UserTitle);

        Intent intent = getIntent();
        LoggedInUserView LoggedInUser = (LoggedInUserView) intent.getSerializableExtra(LoginActivity.EXTRA_USER);
        String FCMToken = (String) intent.getExtras().get(LoginActivity.EXTRA_CODE);
        UserTitle.setText(LoggedInUser.getDisplayName());
        try{
            jsonDelPushInput.put("key",LoggedInUser.getSessionKey());
            jsonDelPushInput.put("call_function","delete_push_config");
            jsonDelPushInput.put("registration_id", "any");
            jsonDelPushInput.put("app_connection_id",1);
            jsonDelPushInput.put("app_version_code",52);
            jsonDelPushInput.put("shop_id",0);
        }catch(Exception e){

        }
        //new MainActivity.ConnectionValidatorTask().execute(jsonDelPushInput);
        try{
            jsonPushInput.put("key",LoggedInUser.getSessionKey());
            jsonPushInput.put("call_function","push_notification_settings");
            jsonPushInput.put("registration_id",FCMToken);
            jsonPushInput.put("app_connection_id",1);
            jsonPushInput.put("api_key","AIzaSyBSh9Z-D0xOo0BdVs5EgSq62v10RhEEHMY");
            jsonPushInput.put("device_unique_id","no_permission_read_phone_state");
            jsonPushInput.put("push_new_customer",1);
            jsonPushInput.put("push_new_order",1);
            jsonPushInput.put("push_order_statuses",-1);
            if (PortoToggle.isChecked()) {
                jsonPushInput.put("porto_enable", "1");
            } else {
                jsonPushInput.put("porto_enable", "0");
            }
            if (MarinaToggle.isChecked()) {
                jsonPushInput.put("marina_enable", "1");
            } else {
                jsonPushInput.put("marina_enable", "0");
            }
        }catch(Exception e){

        }
        new MainActivity.ConnectionValidatorTask().execute(jsonPushInput);
        try{
            jsonOrderInput.put("key",LoggedInUser.getSessionKey());
            jsonOrderInput.put("call_function","get_orders");
            jsonOrderInput.put("orders_from","");
            jsonOrderInput.put("page",1);
            jsonOrderInput.put("sort_by","id");
            jsonOrderInput.put("show_all_customers",1);
            jsonOrderInput.put("currency_code","");
            if (PortoToggle.isChecked()) {
                jsonOrderInput.put("porto_enable", "1");
            } else {
                jsonOrderInput.put("porto_enable", "0");
            }
            if (MarinaToggle.isChecked()) {
                jsonOrderInput.put("marina_enable", "1");
            } else {
                jsonOrderInput.put("marina_enable", "0");
            }
        }catch(Exception e){

        }
        new MainActivity.ConnectionValidatorTask().execute(jsonOrderInput);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.example.elbaeat.ui.login.LoginActivity.class);
                startActivity(intent);
            }
        });

        PortoToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject Input = MainActivity.this.jsonOrderInput;
                Input.remove("porto_enable");
                try{
                    if (PortoToggle.isChecked()) {
                        Input.put("porto_enable", "1");
                    } else {
                        Input.put("porto_enable", "0");
                    }
                }catch(Exception e){

                }
                MainActivity.this.jsonOrderInput = Input;
                new MainActivity.ConnectionValidatorTask().execute(Input);
            }
        });
        MarinaToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject Input = MainActivity.this.jsonOrderInput;
                Input.remove("marina_enable");
                try{
                    if (PortoToggle.isChecked()) {
                        Input.put("marina_enable", "1");
                    } else {
                        Input.put("marina_enable", "0");
                    }
                }catch(Exception e){

                }
                MainActivity.this.jsonOrderInput = Input;
                new MainActivity.ConnectionValidatorTask().execute(Input);
            }
        });


    }


    private class ConnectionValidatorTask extends AsyncTask<JSONObject, Void, String> {
        String jsonOutputString;
        String command;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            PortoToggle.setEnabled(false);
            MarinaToggle.setEnabled(false);
            logoutButton.setEnabled(false);
        }
        protected String doInBackground(JSONObject... params) {
            try {
                JSONObject jsonInput = params[0];
                command = jsonInput.getString("call_function");
                String jsonInputString = jsonInput.toString();
                URL url = new URL("https://elbaeat.it/?connector=mobileassistant");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                jsonOutputString = response.toString();
                System.out.println(jsonOutputString);
            }catch (Exception e) {

            }

            return jsonOutputString;

        }
        protected void onPostExecute(String jsonOutputString) {

            switch(command){
                case "push_notification_settings":

                    break;
                case "get_orders":
                    // TODO: Update Order Info
                    break;
                default:
                    break;
            }
            PortoToggle.setEnabled(true);
            MarinaToggle.setEnabled(true);
            logoutButton.setEnabled(true);

        }
    }
}