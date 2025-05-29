package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mcpro.utils.SessionManager;

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
    RadioGroup roleRadioGroup;
    RadioButton selectedRoleButton;
    TextView sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        //User inputs
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        username = findViewById(R.id.usernametext);
        emailText = findViewById(R.id.email);
        password = findViewById(R.id.Password);
        confirm_password = findViewById(R.id.ConfirmPassword);
        createButton = findViewById(R.id.BtnCreateAccount);
        sign_in = findViewById(R.id.signInLink);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String email = emailText.getText().toString().trim();
                String passwords = password.getText().toString().trim();
                String confirmPasswords = confirm_password.getText().toString().trim();

                if (!isStrongPassword(passwords)) {
                    Toast.makeText(getApplicationContext(), "Weak password! Use at least 8 characters with upper, lower, digit, and special character.", Toast.LENGTH_LONG).show();
                } else if (!passwords.equals(confirmPasswords)) {
                    Toast.makeText(getApplicationContext(), "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedId = roleRadioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(getApplicationContext(), "Please select a role.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedRoleButton = findViewById(selectedId);
                    String role = selectedRoleButton.getText().toString().toLowerCase();
//                  Toast.makeText(getApplicationContext(), "you selected " + role, Toast.LENGTH_SHORT).show();
                    registerUser(name, passwords, email, role);
                }
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly go to MainActivity (you can implement actual login logic here)
                Intent i = new Intent(getApplicationContext(), SignInSignUpPAGE.class);
                startActivity(i);
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

    private void registerUser(String username, String password, String email, String role) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password", password)
                .add("role", role)
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String serverResponse = response.body().string(); //"User registered successfully"
                Log.e("SERVER_RESPONSE", "Code: " + response.code() + " Body: " + serverResponse);

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), serverResponse, Toast.LENGTH_SHORT).show();

                        if(serverResponse.toLowerCase().contains("success")) {
                            if (role.equals("counsellor")){
                                SessionManager sessionManager = new SessionManager(SignUpPage.this);
                                sessionManager.setUsername(username);
                                Intent i = new Intent(getApplicationContext(), CheckProfileActivity.class);
                                i.putExtra("name", username);
                                startActivity(i);
                                finish();
                            } else {
                                SessionManager sessionManager = new SessionManager(SignUpPage.this);
                                sessionManager.setUsername(username);
                                Intent i = new Intent(getApplicationContext(), CheckIssueSelection.class);
                                i.putExtra("name", username);
                                startActivity(i);
                                finish();
                            }

                        }
                    });
                } else {
                    runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Server Error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

}
