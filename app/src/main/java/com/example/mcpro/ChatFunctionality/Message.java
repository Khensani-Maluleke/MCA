package com.example.mcpro.ChatFunctionality;

public class Message {
    private String content;
    private boolean isSent; // true = sent, false = received
    private String timestamp;

    public Message(String content, boolean isSent, String timestamp) {
        this.content = content;
        this.isSent = isSent;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
