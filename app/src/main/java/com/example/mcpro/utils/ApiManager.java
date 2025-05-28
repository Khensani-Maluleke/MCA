package com.example.mcpro.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.widget.Toast;

import com.example.mcpro.utils.CounsellorData;
import com.example.mcpro.CounsellorHomeActivity; // 👈 Your next screen

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class ApiManager {

    public static void submitCounsellorData(Context context) {
        OkHttpClient client = new OkHttpClient();

        CounsellorData data = CounsellorData.getInstance();

        RequestBody formBody = new FormBody.Builder()
                .add("name", data.getName())
                .add("email", data.getEmail())
                .add("bio", data.getBio())
                .add("issues", String.join(",", data.getIssues()))
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2826175/test.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Failed to send data: " + e.getMessage());
                // Optional: show error Toast (on UI thread)
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                if (response.isSuccessful()) {
                    ((Activity) context).runOnUiThread(() -> {
                        //Save login session
                        SessionManager sessionManager = new SessionManager(context);
                        sessionManager.setLogin(true);
                        sessionManager.setProfileDone(true);

                        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show();
                        // ✅ Navigate to the new Activity
                        Intent intent = new Intent(context, CounsellorHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    });
                } else {
                    Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Unexpected response: " + response.code());
                }
            }
        });
    }
}

