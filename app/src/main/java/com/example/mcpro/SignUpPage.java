package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
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

public class SignUpPage extends AppCompatActivity {
    String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/userSignup.php";
    EditText username;
    EditText emailText;
    EditText confirm_password;
    EditText password;
    Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        //User inputs
        username = findViewById(R.id.usernametext);
        emailText = findViewById(R.id.email);
        password = findViewById(R.id.Password);
        confirm_password = findViewById(R.id.ConfirmPassword);
        createButton = findViewById(R.id.BtnCreateAccount);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                String email = emailText.getText().toString();
                String passwords = password.getText().toString();
                String confirmPasswords = confirm_password.getText().toString();

                if (!isStrongPassword(passwords)) {
                    Toast.makeText(getApplicationContext(), "Weak password! Use at least 8 characters with upper, lower, digit, and special character.", Toast.LENGTH_LONG).show();
                } else if (!passwords.equals(confirmPasswords)) {
                    Toast.makeText(getApplicationContext(), "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, passwords, email);
                }
            }
        });
    }

    public static boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".indexOf(c) >= 0) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void registerUser(String username, String password, String email) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    String postData = "username=" + URLEncoder.encode(username, "UTF-8")
                            + "&email=" + URLEncoder.encode(email, "UTF-8")
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
                            Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_SHORT).show();

                            if (serverResponse.toLowerCase().contains("success")) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("name", username);
                                startActivity(intent);
                                finish();
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
            }
        });
        thread.start();
    }
}
