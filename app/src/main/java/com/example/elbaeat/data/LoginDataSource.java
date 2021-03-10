package com.example.elbaeat.data;

import android.os.AsyncTask;
import android.os.Build;

import com.example.elbaeat.data.model.LoggedInUser;
import com.google.common.hash.Hashing;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

public class LoginDataSource {

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    protected Result<LoggedInUser> login(String username, String password) {

        try {
            URL url = new URL("https://elbaeat.it/?connector=mobileassistant");
            // TODO: handle loggedInUser authentication
            String hashedPW =  Hashing.md5()
                    .hashString(password, StandardCharsets.UTF_8)
                    .toString();
            String completeHash = Hashing.sha256()
                    .hashString(username + hashedPW, StandardCharsets.UTF_8)
                    .toString();
            String jsonOutputString;
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("device_name",getDeviceName());
            jsonInput.put("registration_id","");
            jsonInput.put("device_unique_id","no_permission_read_phone_state");
            jsonInput.put("hash",completeHash);
            jsonInput.put("key","");
            jsonInput.put("call_function","get_version");
            String jsonInputString = jsonInput.toString();


            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }catch (Exception e) {
                return new Result.Error(new IOException("Error sending log in information: " + e.getMessage(), e));
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                jsonOutputString = response.toString();
                System.out.println(jsonOutputString);
            }catch (Exception e) {
                return new Result.Error(new IOException("Error reading log in response", e));
            }
            JSONObject jo = new JSONObject(jsonOutputString);

            String sessionKey = jo.getString("session_key");

            LoggedInUser fakeUser =
                    new LoggedInUser(
                            username,
                            sessionKey);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}