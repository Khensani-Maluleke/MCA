package com.example.mcpro.ChatFunctionality;

public class Message {
    private String content;
    private boolean isSent; // true = sent, false = received

    public Message(String content, boolean isSent) {
        this.content = content;
        this.isSent = isSent;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }
}

