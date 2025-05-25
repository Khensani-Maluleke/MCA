package com.example.mcpro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassword extends AppCompatActivity {

    EditText email;
    Button reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.emailAddress);
        reset = findViewById(R.id.BtnReset);


        //Onclick
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailS = email.getText().toString().trim();
                if(emailS.isEmpty()) {//similar to emailS.equals("")
                    Toast.makeText(getApplicationContext(), "Please enter your email on the required field", Toast.LENGTH_SHORT).show();
                } else {
                    sendResetRequest(emailS);
                }

            }
        });
    }

    private void sendResetRequest(String email) {
        String URL = "https://lamp.ms.wits.ac.za/home/s2815983/request_password_reset.php";

        OkHttpClient client = new OkHttpClient();

        // Form body with email parameter
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .build();

        // Build the POST request
        Request request = new Request.Builder()
                .url(URL)
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

                Log.d("DEBUG_RESPONSE", "Code: " + response.code() + ", Body: " + res);
                if (response.isSuccessful()) {
                    System.out.println("I am on onResponse");
                    // This also runs on a background thread
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show()
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