package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpro.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnterCodeActivity extends AppCompatActivity {

    private TextInputEditText digit1, digit2, digit3, digit4, digit5, digit6;
    private MaterialButton btnVerify;
    String verifyTokenURL = "https://lamp.ms.wits.ac.za/home/s2815983/verifyToken.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code); // Link to your XML

        digit1 = findViewById(R.id.codeDigit1);
        digit2 = findViewById(R.id.codeDigit2);
        digit3 = findViewById(R.id.codeDigit3);
        digit4 = findViewById(R.id.codeDigit4);
        digit5 = findViewById(R.id.codeDigit5);
        digit6 = findViewById(R.id.codeDigit6);
        btnVerify = findViewById(R.id.btnVerify);

        // Set listeners for automatic focus shift
        setupInputWatcher(digit1, digit2);
        setupInputWatcher(digit2, digit3);
        setupInputWatcher(digit3, digit4);
        setupInputWatcher(digit4, digit5);
        setupInputWatcher(digit5, digit6);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = getEnteredCode().trim();
                if (code.length() == 6) {
                    // Send to backend or verify
                    //verifyToken(code);
                    // TODO: Send `code` to your backend for validation
                    new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                            .setTitle("Code")
                            .setMessage("You entered the code " + code)
                            .setIcon(R.drawable.error_icon) // Replace with your own icon
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                //startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                            })
                            .show();
                } else {
                    Toast.makeText(EnterCodeActivity.this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setupInputWatcher(final TextInputEditText current, final TextInputEditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }
        });
    }

    private String getEnteredCode() {
        return digit1.getText().toString() +
                digit2.getText().toString() +
                digit3.getText().toString() +
                digit4.getText().toString() +
                digit5.getText().toString() +
                digit6.getText().toString();
    }

    private void verifyToken(String token) {

        OkHttpClient client = new OkHttpClient();
        // Get user's details from session
        SessionManager sessionManager = new SessionManager(EnterCodeActivity.this);
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();

        if (email.equals("") || role.equals("")) {
            new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                    .setTitle("Invalid Attempt")
                    .setMessage("Missing email or role in session. Please try again.")
                    .setIcon(R.drawable.error_icon) // Replace with your own icon
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                    })
                    .show();
            return;
        }



        // Build the form body
        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .add("email", email)
                .add("role", role)
                .build();

        // Build the POST request
        Request request = new Request.Builder()
                .url(verifyTokenURL)
                .post(formBody)
                .build();

        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                        .setTitle("Network Error")
                        .setMessage("Something went wrong: " + e.getMessage())
                        .setIcon(R.drawable.error_icon)
                        .setPositiveButton("Retry", (dialog, which) -> dialog.dismiss())
                        .show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string().trim(); // trim to avoid whitespace issues

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        if (res.contains("successful")) {
                            new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                                    .setTitle("Success")
                                    .setMessage("Code verification successful.")
                                    .setIcon(R.drawable.success_icon)
                                    .setPositiveButton("Continue", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), NewPasswordCreation.class);
                                        startActivity(intent);
                                    })
                                    .show();
                        } else {
                            new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                                    .setTitle("Verification Failed")
                                    .setMessage(res) // backend message: "Token verification failed.", etc.
                                    .setIcon(R.drawable.error_icon)
                                    .setPositiveButton("Retry", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    } else {
                        new MaterialAlertDialogBuilder(EnterCodeActivity.this)
                                .setTitle("Server Error")
                                .setMessage("Server returned an error. Please try again.")
                                .setIcon(R.drawable.error_icon)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                });
            }
        });
    }

}
