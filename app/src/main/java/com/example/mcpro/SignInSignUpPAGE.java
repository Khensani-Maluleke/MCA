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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignInSignUpPAGE extends AppCompatActivity {

    TextView sign_up;
    Button login;
    EditText pass;
    EditText user;

    // Your PHP endpoint
    String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/userSignin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_or_signin);

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
                    registerUser(username, password);
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
    }

    private void registerUser(String username, String password) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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