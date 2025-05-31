package com.example.mcpro.ChatFunctionality;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mcpro.CheckProfileActivity;
import com.example.mcpro.Consellorpage;
import com.example.mcpro.R;
import com.example.mcpro.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton backButton;
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private String sessionId;
    TextView partnerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionId = String.valueOf(getIntent().getIntExtra("session_id",0));
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.imageButton);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        partnerName = findViewById(R.id.chatWithName);
        fetchMessage(sessionId);
        //int session_id = getIntent().getIntExtra("session_id",0);
        String name = getIntent().getStringExtra("partner_name");
        SessionManager sessionManager = new SessionManager(ChatActivity.this);
        String sender_key = sessionManager.getRole();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = messageInput.getText().toString().trim();
                if (!content.isEmpty()) {
                    messageList.add(new Message(content, true)); // sent message
                    chatAdapter.notifyItemRangeInserted(messageList.size() - 1 , 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    sendMessage(sessionId, content);
                    messageInput.setText("");
                }
            }
        });
        Toast.makeText(this, sender_key, Toast.LENGTH_SHORT).show();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatWindow.class);
                startActivity(intent);
                finish();
            }
        });
        changeName(name);
    }

    private void changeName(String name) {
        runOnUiThread(() -> {
            partnerName.setText("Chat session with "+ name);
        });
    }

    void sendMessage(String sessionId, String message) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://lamp.ms.wits.ac.za/home/s2815983/send_message.php";

        SessionManager sessionManager = new SessionManager(ChatActivity.this);
        String key = sessionManager.getRole();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ChatActivity", "Message sent. Refreshing messages.");
            fetchMessage(sessionId);
        }, error -> Toast.makeText(ChatActivity.this, "Failed to send message." + error.toString(), Toast.LENGTH_SHORT).show()) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", sessionId);
                params.put("chat", message);
                params.put("status", "1");
                params.put("sender_key", key);
                return params;
            }
        };
        queue.add(postRequest);
    }

    void fetchMessage(String sessionId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://lamp.ms.wits.ac.za/home/s2815983/get_message.php?session_id="+sessionId;

        SessionManager sessionManager = new SessionManager(ChatActivity.this);
        String key = sessionManager.getRole();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET , url , null,
                response->{
                    messageList.clear();
                    for(int i = 0 ; i < response.length() ; i++){
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            boolean isUser = obj.getInt("status") == 1;
                            String msg = obj.getString("chat");

                            messageList.add(new Message(msg,true));
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                },
                error->{Toast.makeText(ChatActivity.this, "Failed to fetch messages.", Toast.LENGTH_SHORT).show();}
        );
        queue.add(request);
    }
}
//package com.example.mcpro.ChatFunctionality;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.mcpro.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private RecyclerView chatRecyclerView;
//    private EditText messageInput;
//    private ImageButton sendButton;
//    private ChatAdapter chatAdapter;
//    private List<Message> messageList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        chatRecyclerView = findViewById(R.id.chatRecyclerView);
//        messageInput = findViewById(R.id.messageInput);
//        sendButton = findViewById(R.id.sendButton);
//
//        messageList = new ArrayList<>();
//        chatAdapter = new ChatAdapter(messageList);
//
//        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        chatRecyclerView.setAdapter(chatAdapter);
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = messageInput.getText().toString().trim();
//                if (!content.isEmpty()) {
//                    messageList.add(new Message(content, true)); // sent message
//                    messageList.add(new Message("Echo: " + content, false)); // simulate reply
//
//                    chatAdapter.notifyItemRangeInserted(messageList.size() - 2, 2);
//                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
//                    messageInput.setText("");
//                }
//            }
//        });
//    }
//}
