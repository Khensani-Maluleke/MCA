package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mcpro.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPasswordCreation extends AppCompatActivity {

    EditText pwd1;
    EditText pwd2;
    Button savePwd;
    String updateUserURL = "https://lamp.ms.wits.ac.za/home/s2815983/updateUser.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password_creation);

        pwd1 = findViewById(R.id.NewPassword);
        pwd2 = findViewById(R.id.NewConfirmPassword);
        savePwd = findViewById(R.id.BtnSavePassword);

        SessionManager sessionManager = new SessionManager(NewPasswordCreation.this);
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();

        savePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwd1.getText().toString();
                String confirmPassword = pwd2.getText().toString();

                if (!isStrongPassword(password)) {
                    Toast.makeText(getApplicationContext(), "Weak password! Use at least 8 characters with upper, lower, digit, and special character.", Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
                } else {
                    updateUser(password, email, role);
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

    private void updateUser(String newPassword, String email, String role) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("newPassword", newPassword)
                .add("role", role)
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(updateUserURL)
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
                        new MaterialAlertDialogBuilder(NewPasswordCreation.this)
                                .setTitle("Success")
                                .setMessage("Password successfully changed. Now you can log in with your new password")
                                .setIcon(R.drawable.success_icon)
                                .setPositiveButton("Continue", (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), SignInSignUpPAGE.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
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