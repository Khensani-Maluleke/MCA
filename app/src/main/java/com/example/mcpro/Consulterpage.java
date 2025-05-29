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

import com.example.mcpro.utils.SessionManager;

import org.json.JSONArray;
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
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(), CounsellorHomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else if (responseJson.has("error")) {
                                String errorMsg = responseJson.getString("error");
                                Toast.makeText(getApplicationContext(), "Server Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Server returned status code: " + response.code(), Toast.LENGTH_SHORT).show();
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
}