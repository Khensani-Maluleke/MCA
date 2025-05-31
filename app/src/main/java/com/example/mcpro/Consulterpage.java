package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpro.ChatFunctionality.ChatActivity;
import com.example.mcpro.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Consulterpage extends AppCompatActivity {
    RadioGroup radioGroup;
    Button subb;
    String storeConsultorIssueURL = "https://lamp.ms.wits.ac.za/home/s2815983/storeIssue.php";
    String assignCounsellorURL = "https://lamp.ms.wits.ac.za/home/s2815983/assign_counsellor.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consulterpage);

        radioGroup = findViewById(R.id.radioGroup);
        subb=findViewById(R.id.counsultersubbtn);

        subb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRadio = findViewById(selectedId);
                    String selectedText = selectedRadio.getText().toString().trim();
                    storeIssue(selectedText);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeIssue(String issue) {
        OkHttpClient client = new OkHttpClient();

        SessionManager sessionManager = new SessionManager(Consulterpage.this);
        String username = sessionManager.getUsername();
        // Construct JSON object
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("issue", issue);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(storeConsultorIssueURL)
                .post(requestBody)
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
                String serverResponse = response.body().string();
                Log.e("SERVER_RESPONSE", "Code: " + response.code() + " Body: " + serverResponse);

                runOnUiThread(() -> {
                    try {
                        JSONObject responseJson = new JSONObject(serverResponse);

                        if (response.isSuccessful()) {
                            if (responseJson.has("success")) {
                                String message = responseJson.getString("success");
                                int consultor_id = responseJson.getInt("consultor_id");
                                int issue_id = responseJson.getInt("issue_id");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                assignConsultor(consultor_id, issue_id);
                            } else if (responseJson.has("error")) {
                                String errorMsg = responseJson.getString("error");
                                Toast.makeText(getApplicationContext(), "Server Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Server returned status code: " + responseJson, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(), "Server returned : " + response.body().string(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("ERROR_MESSAGE_PLEASE_CHECK", e.getMessage());
                        Toast.makeText(getApplicationContext(), "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void assignConsultor(int consultorId, int issueId) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("consultor_id", consultorId);
            jsonObject.put("issue_id", issueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(assignCounsellorURL) // replace with actual URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure (show error on UI thread)
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string(); // This must happen before runOnUiThread

                if (response.isSuccessful()) {
                    Log.d("AssignAPI", responseBody);

                    runOnUiThread(() -> {
                        new MaterialAlertDialogBuilder(Consulterpage.this)
                                .setTitle("Counsellor found!")
                                .setMessage("You have been successfully linked to a counsellor. Click Ok to proceed!")
                                .setIcon(R.drawable.success_icon) // Replace with your own icon
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                })
                                .show();
                    });

                } else {
                    Log.e("AssignAPI", "Error: " + response.code());
                }
            }

        });
    }

}