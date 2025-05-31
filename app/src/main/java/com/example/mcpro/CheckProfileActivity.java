package com.example.mcpro;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpro.ChatFunctionality.ChatActivity;
import com.example.mcpro.ChatFunctionality.ChatWindow;
import com.example.mcpro.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckProfileActivity extends AppCompatActivity {

    String isprofileCompletedURL = "https://lamp.ms.wits.ac.za/home/s2815983/isprofileCompleted.php";
    ProgressBar progressBar;
    String username; // Pass this from LoginActivity or store in SharedPreferences
    private static final int DELAY_MILLIS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_profile);

        progressBar = findViewById(R.id.progressBar);

        // Assume username is stored in SharedPreferences
        SessionManager sessionManager = new SessionManager(CheckProfileActivity.this);
        String username = sessionManager.getUsername();

        if (username != null) {
            //checkProfileCompletion(username);
            new Handler().postDelayed(() -> checkProfileCompletion(username), DELAY_MILLIS);
        } else {
            // fallback in case username not found
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInSignUpPAGE.class));
        }
    }

    private void checkProfileCompletion(String username) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .build();

        Request request = new Request.Builder()
                .url(isprofileCompletedURL) // replace with your real URL
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CheckProfileActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        JSONObject json = new JSONObject(res);
                        boolean profileComplete = json.getBoolean("profile_complete");

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (profileComplete) {
                                startActivity(new Intent(CheckProfileActivity.this, ChatWindow.class));
                            } else {
                                startActivity(new Intent(CheckProfileActivity.this, Consellorpage.class));
                            }
                            finish();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CheckProfileActivity.this, "Invalid response", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CheckProfileActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
