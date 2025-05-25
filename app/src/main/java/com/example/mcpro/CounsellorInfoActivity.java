package com.example.mcpro;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CounsellorInfoActivity extends AppCompatActivity {

    private EditText bioInput, experienceInput, availabilityInput;
    private Button nextButton;
    private TextView pageTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellor_info);

        bioInput = findViewById(R.id.bioInput);
        experienceInput = findViewById(R.id.experienceInput);
        availabilityInput = findViewById(R.id.availabilityInput);
        pageTracker = findViewById(R.id.pageTracker);
        nextButton = findViewById(R.id.nextButton);

        pageTracker.setText("Step 1 of 2");

        nextButton.setOnClickListener(v -> {
            String bio = bioInput.getText().toString().trim();
            String exp = experienceInput.getText().toString().trim();
            String availability = availabilityInput.getText().toString().trim();

            if (bio.isEmpty() || exp.isEmpty() || availability.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CounsellorIssuesActivity.class);
            intent.putExtra("bio", bio);
            intent.putExtra("experience", exp);
            intent.putExtra("availability", availability);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }
}
