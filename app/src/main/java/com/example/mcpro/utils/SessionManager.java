package com.example.mcpro.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "userSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ISSUES_DONE = "issuesCompleted";
    private static final String KEY_PROFILE_DONE = "profileCompleted";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    //set user login state
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // I am storing issues status
    public void setIssuesDone(boolean done) {
        editor.putBoolean(KEY_ISSUES_DONE, done);
        editor.apply();
    }

    public void setProfileDone(boolean done) {
        editor.putBoolean(KEY_PROFILE_DONE, done);
        editor.apply();
    }

    public boolean isIssuesDone() {
        return prefs.getBoolean(KEY_ISSUES_DONE, false);
    }

    public boolean isProfileDone() {
        return prefs.getBoolean(KEY_PROFILE_DONE, false);
    }

    // Store username
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    // Store email
    public void setEmail(String email) {
        editor.putString(KEY_USERNAME, email);
        editor.apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    // Store user role
    public void setRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
