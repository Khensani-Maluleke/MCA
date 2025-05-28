package com.example.mcpro.utils;

import java.util.ArrayList;

public class CounsellorData {
    private static CounsellorData instance;

    private String name;
    private String email;
    private String bio;
    private String avatar; // You can use a URL or local file path
    private ArrayList<String> issues;

    private CounsellorData() {
        issues = new ArrayList<>();
    }

    public static CounsellorData getInstance() {
        if (instance == null) {
            instance = new CounsellorData();
        }
        return instance;
    }

    // Setters and Getters below...

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setIssues(ArrayList<String> issues) { this.issues = issues; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getAvatar() { return avatar; }
    public ArrayList<String> getIssues() { return issues; }
}
