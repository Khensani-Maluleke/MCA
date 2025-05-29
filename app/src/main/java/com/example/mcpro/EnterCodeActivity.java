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

        //Get user's details from session
        SessionManager sessionManager = new SessionManager(EnterCodeActivity.this);
        sessionManager.getEmail();
        sessionManager.getRole();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = getEnteredCode();
                if (code.length() == 6) {
                    // Send to backend or verify
                    Toast.makeText(EnterCodeActivity.this, "Entered code: " + code, Toast.LENGTH_SHORT).show();
                    verifyToken(code);
                    // TODO: Send `code` to your backend for validation
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
        //Get user's details from session
        SessionManager sessionManager = new SessionManager(EnterCodeActivity.this);
        String email = sessionManager.getEmail();
        String role = sessionManager.getRole();
        if (email.equals("") || role.equals("")) {
            Toast.makeText(getApplicationContext(), "Invalid attempt to reset password", Toast.LENGTH_SHORT).show();
            Intent intent  = new Intent(getApplicationContext(), ForgotPassword.class);
            startActivity(intent);
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // Form body with email parameter
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

        // Make the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // This runs on a background thread
                Log.d("error", e.toString());
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();  // Always get the body first

                if (response.isSuccessful()) {

                    runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), NewPasswordCreation.class);
                                startActivity(intent);
                            }
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Server error.", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
