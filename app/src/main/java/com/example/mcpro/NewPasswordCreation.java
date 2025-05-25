package com.example.mcpro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewPasswordCreation extends AppCompatActivity {

    EditText pwd1;
    EditText pwd2;
    Button savePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password_creation);

        pwd1 = findViewById(R.id.NewPassword);
        pwd2 = findViewById(R.id.NewConfirmPassword);
        savePwd = findViewById(R.id.BtnSavePassword);

        savePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwd1.getText().toString();
                String confirmPassword = pwd2.getText().toString();

                if (!isStrongPassword(password)) {
                    Toast.makeText(getApplicationContext(), "Weak password! Use at least 8 characters with upper, lower, digit, and special character.", Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "PASSWORDS DON'T MATCH", Toast.LENGTH_SHORT).show();
                } else {
                    //registerUser(name, passwords, email);
                }
            }
        });

    }

    public static boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".indexOf(c) >= 0) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}