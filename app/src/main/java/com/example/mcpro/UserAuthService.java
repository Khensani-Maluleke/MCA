// File: UserAuthService.java
package com.example.mcpro;

import android.content.Context;
import android.widget.Toast;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UserAuthService {
    private static final String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/userSignin.php";

    public static void loginUser(Context context, String username, String password) {
        new Thread(() -> {
            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "username=" + URLEncoder.encode(username, "UTF-8")
                        + "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String serverResponse = response.toString();

                    ((SignInSignUpPAGE) context).runOnUiThread(() -> {
                        Toast.makeText(context, serverResponse, Toast.LENGTH_SHORT).show();

                        if (serverResponse.toLowerCase().contains("success")) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("name", username);
                            context.startActivity(intent);
                            ((SignInSignUpPAGE) context).finish();
                        }
                    });
                } else {
                    ((SignInSignUpPAGE) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Server Error: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((SignInSignUpPAGE) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

}
