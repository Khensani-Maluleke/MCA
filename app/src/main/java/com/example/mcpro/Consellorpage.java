package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpro.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Consellorpage extends AppCompatActivity {
    CheckBox Anxiety;
    CheckBox Addiction;
    CheckBox Abuse;
    CheckBox Academics;
    CheckBox Depression;
    CheckBox General;
    CheckBox GriefLoss;
    Button sub;
    String save_issuesURL = "https://lamp.ms.wits.ac.za/home/s2815983/completeCounsellorProfile.php";
    ArrayList<String>issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consellorpage);
        //FINDING ID
        issues = new ArrayList<>();
        Anxiety=findViewById(R.id.AnxietyID);
        Addiction=findViewById(R.id.AddictionID);
        Abuse=findViewById(R.id.AbuseID);
        Academics=findViewById(R.id.AcademicID);
        Depression=findViewById(R.id.DeprissionID);
        General=findViewById(R.id.GeneralID);
        GriefLoss=findViewById(R.id.Grief_LossID);
        sub=findViewById(R.id.consellorsubbtn);


        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issues = new ArrayList<>();
                //CHECKING STATE
                Boolean checkAnxietyState=Anxiety.isChecked();
                Boolean checkAddiction=Addiction.isChecked();
                Boolean checkAbuseState=Abuse.isChecked();
                Boolean checkAcademicsState=Academics.isChecked();
                Boolean checkDepressionState=Depression.isChecked();
                Boolean checkGeneralState=General.isChecked();
                Boolean checkGriefLossState=GriefLoss.isChecked();

                if(checkAnxietyState){
                    issues.add( Anxiety.getText().toString());
                }
                if(checkAddiction){
                    issues.add( Addiction.getText().toString());
                }
                if(checkAbuseState){
                    issues.add( Abuse.getText().toString());
                }
                if(checkAcademicsState){
                    issues.add( Academics.getText().toString());
                }
                if(checkDepressionState){
                    issues.add( Depression.getText().toString());
                }
                if(checkGeneralState){
                    issues.add( General.getText().toString());
                }
                if(checkGriefLossState){
                    issues.add(GriefLoss.getText().toString());
                }

                SessionManager sessionManager = new SessionManager(Consellorpage.this);
                //Toast.makeText(getApplicationContext(), "Welcome, " + sessionManager.getUsername() + ". "+ sessionManager.getRole(), Toast.LENGTH_SHORT).show();
                saveIssues();
            }
        });
    }

    private void saveIssues() {
        OkHttpClient client = new OkHttpClient();

        SessionManager sessionManager = new SessionManager(Consellorpage.this);
        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();
        // Construct JSON object
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("role", role);

            JSONArray jsonArray = new JSONArray();
            for (String issue : issues) {
                jsonArray.put(issue);
            }
            jsonObject.put("issues", jsonArray);
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
                .url(save_issuesURL)
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