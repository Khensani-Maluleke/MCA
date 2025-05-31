package com.example.mcpro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mcpro.fragments.ActiveChatsFragment;
import com.example.mcpro.fragments.PastChatsFragment;
import com.example.mcpro.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_active_chats) {
                selectedFragment = new ActiveChatsFragment();
            } else if (itemId == R.id.nav_past_chats) {
                selectedFragment = new PastChatsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            } else {
                return false;
            }
        });


        // Default fragment
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_active_chats);
        }
    }
}
