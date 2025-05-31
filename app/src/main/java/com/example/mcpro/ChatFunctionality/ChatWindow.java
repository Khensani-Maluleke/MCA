package com.example.mcpro.ChatFunctionality;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpro.R;
import com.example.mcpro.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatWindow extends AppCompatActivity {

    LinearLayout sessionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);

        sessionContainer = findViewById(R.id.sessionContainer);

        SessionManager sessionManager = new SessionManager(ChatWindow.this);
        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();

        fetchSessions(username, role);
    }

    void fetchSessions(String userName, String role){
        OkHttpClient client = new OkHttpClient();

        String url = "https://lamp.ms.wits.ac.za/home/s2815983/get_sessions.php?username=" + userName + "&role=" + role;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() ->
                        Toast.makeText(ChatWindow.this, "Failed to fetch sessions", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());

                    mainHandler.post(() -> {
                        sessionContainer.removeAllViews();  // Clear previous if needed

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String name = obj.getString("partner_name");
                                int session_id = obj.getInt("session_id");
                                String chat = obj.optString("chat", "");

                                // Create the root layout for each chat
                                LinearLayout chatLayout = new LinearLayout(ChatWindow.this);
                                chatLayout.setOrientation(LinearLayout.HORIZONTAL);
                                chatLayout.setBackgroundColor(0xFFE3F2FD); // match XML light blue
                                LinearLayout.LayoutParams chatParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                chatParams.setMargins(0, 0, 0, 8);
                                chatLayout.setLayoutParams(chatParams);
                                chatLayout.setPadding(12, 12, 12, 12);
                                chatLayout.setId(View.generateViewId()); // unique ID

                                // Profile Icon
                                ImageView icon = new ImageView(ChatWindow.this);
                                icon.setImageResource(android.R.drawable.sym_def_app_icon);
                                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(100, 100);
                                iconParams.gravity = Gravity.CENTER_VERTICAL;
                                icon.setLayoutParams(iconParams);
                                chatLayout.addView(icon);

                                // Text Container
                                LinearLayout textContainer = new LinearLayout(ChatWindow.this);
                                textContainer.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        1f
                                );
                                textParams.setMargins(12, 0, 0, 0);
                                textContainer.setLayoutParams(textParams);

                                // Name
                                TextView nameText = new TextView(ChatWindow.this);
                                nameText.setText(name);
                                nameText.setTextColor(0xFF000000);
                                nameText.setTextSize(16f);
                                nameText.setTypeface(null, android.graphics.Typeface.BOLD);

                                // Chat preview
                                TextView chatText = new TextView(ChatWindow.this);
                                chatText.setText(chat);
                                chatText.setTextColor(0xFF555555);
                                chatText.setTextSize(14f);

                                textContainer.addView(nameText);
                                textContainer.addView(chatText);
                                chatLayout.addView(textContainer);
                                sessionContainer.addView(chatLayout);

                                // ✅ Click Listener
                                String finalName = name; // make it effectively final
                                chatLayout.setOnClickListener(view -> {
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("session_id", session_id);
                                    intent.putExtra("partner_name", name);
                                    startActivity(intent);

                                    // Example: Launch a new ChatDetail activity
                                    /*
                                    Intent intent = new Intent(ChatWindow.this, ChatDetailActivity.class);
                                    intent.putExtra("partner_name", finalName);
                                    startActivity(intent);
                                    */
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

//package com.example.mcpro.ChatFunctionality;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.mcpro.R;
//import com.example.mcpro.utils.SessionManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class ChatWindow extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_chat_window);
//
//        SessionManager sessionManager = new SessionManager(ChatWindow.this);
//        String username = sessionManager.getUsername();
//        String role = sessionManager.getRole();
//        fetchSessions(username, role);
//    }
//
//    void fetchSessions(String userName, String role){
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://lamp.ms.wits.ac.za/home/s2815983/get_sessions.php?username="+userName+"?role="+role;
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET , url , null,
//                response->{
//                    messageList.clear();
//                    for(int i = 0 ; i < response.length() ; i++){
//                        try {
//                            JSONObject obj = response.getJSONObject(i);
//                            boolean isUser = obj.getInt("status") == 1;
//                            String msg = obj.getString("chat");
//
//                            messageList.add(new Message(msg,isUser));
//                        } catch(JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    chatAdapter.notifyDataSetChanged();
//                },
//                error->{
//                    Toast.makeText(ChatWindow.this, "Failed to fetch messages.", Toast.LENGTH_SHORT).show();
//        });
//        queue.add(request);
//    }
//}