package com.example.mcpro;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInSignUpPAGE extends AppCompatActivity {

    TextView sign_up;
    Button login;
    EditText pass;
    EditText user;
    TextView forgotPwd;

    // Your PHP endpoint
    String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/userSignin.php";
    String userRoleURL = "https://lamp.ms.wits.ac.za/home/s2815983/userRoleURL.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_or_signin);

        forgotPwd = findViewById(R.id.forgotPasswordLink);
        sign_up = findViewById(R.id.signUpLink);
        login = findViewById(R.id.BtnLogin);
        user = findViewById(R.id.usernametext);
        pass = findViewById(R.id.passwordtext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "ENTER BOTH THE USERNAME AND PASSWORD.", Toast.LENGTH_SHORT).show();
                } else {
                    logUser(username, password);
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly go to MainActivity (you can implement actual login logic here)
                Intent i = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(i);
            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly go to MainActivity (you can implement actual login logic here)
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            }
        });
    }

private void logUser(String username, String password) {
    Thread thread = new Thread(() -> {
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
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String serverResponse = response.toString();

                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(serverResponse);
                        if (jsonResponse.has("message") && jsonResponse.getString("message").toLowerCase().contains("success")) {
                            String role = jsonResponse.getString("role").toLowerCase();

                            Toast.makeText(getApplicationContext(), "Welcome, " + role, Toast.LENGTH_SHORT).show();

                            if (role.equals("counsellor")) {
                                Intent intent = new Intent(getApplicationContext(), Consellorpage.class);
                                startActivity(intent);
                            } else if (role.equals("consulter")) {
                                Intent intent = new Intent(getApplicationContext(), Consulterpage.class);
                                startActivity(intent);
                            }

                        } else if (jsonResponse.has("error")) {
                            Toast.makeText(getApplicationContext(), jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unexpected response", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "JSON Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });

            } else {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Server Error: " + responseCode, Toast.LENGTH_SHORT).show();
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    });
    thread.start();
}

//    private void logUser(String username, String password) {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(serverURL);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    conn.setDoOutput(true);
//
//                    String postData = "username=" + URLEncoder.encode(username, "UTF-8")
//                            + "&password=" + URLEncoder.encode(password, "UTF-8");
//
//                    OutputStream os = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                    writer.write(postData);
//                    writer.flush();
//                    writer.close();
//                    os.close();
//
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        String inputLine;
//                        StringBuilder response = new StringBuilder();
//
//                        while ((inputLine = in.readLine()) != null) {
//                            response.append(inputLine);
//                        }
//                        in.close();
//
//                        String serverResponse = response.toString();
//
//                        runOnUiThread(() -> {
//                            Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_SHORT).show();
//
//                            if (serverResponse.toLowerCase().contains("success")) {
//
//
//                            }
//                        });
//                    } else {
//                        runOnUiThread(() -> {
//                            Toast.makeText(getApplicationContext(), "Server Error: " + responseCode, Toast.LENGTH_SHORT).show();
//                        });
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    runOnUiThread(() -> {
//                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    });
//                }
//            }
//        });
//        thread.start();
//    }
}