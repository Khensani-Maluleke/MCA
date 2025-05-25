//package com.example.mcpro;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SignInSignUpPAGE extends AppCompatActivity {
//    Button sign_up;
//    Button login;
//    EditText pass;
//    EditText user;
//    String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/signup.php";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_signup_or_signin);
//        sign_up=findViewById(R.id.BtnSignUp);
//        login =findViewById(R.id.BtnLogin);
//        user=findViewById(R.id.usernametext);
//        pass=findViewById(R.id.passwordtext);
//
//         sign_up.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 if (pass.getText().toString().equals("") || user.getText().toString().equals("")) {
//                     CharSequence error="ENTER BOTH THE USERNAME AND PASSWORD.";
//                     Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();//This is a popup message. You don't need a TextView for this text!
//                     //System.out.println("ENTER BOTH THE USERNAME AND PASSWORD.");
//                 } else {
//                     post(user.getText().toString(), pass.getText().toString());
//                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                     intent.putExtra("name", user.getText().toString());//sending the name entered on the form to the second screen.
//                     startActivity(intent);
//                     finish();//prevents the app from going back to the Login page when you press the back button.
//                     //You have to log out in order to return to the Login Page
//                 }
//             }
//         });
//
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent i=new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//            }
//    });
//}}

package com.example.mcpro;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SignInSignUpPAGE extends AppCompatActivity {

    Button login;
    EditText pass;
    EditText user;
    TextView sign_up;
    TextView forgotPassword;
    // Your PHP endpoint
    String serverURL = "https://lamp.ms.wits.ac.za/home/s2815983/userSignin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_or_signin);

        //Code to store the user's session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        sign_up = findViewById(R.id.signUpLink);
        forgotPassword = findViewById(R.id.forgotPasswordLink);
        login = findViewById(R.id.BtnLogin);
        user = findViewById(R.id.usernametext);
        pass = findViewById(R.id.passwordtext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "ENTER BOTH THE USERNAME AND PASSWORD.", Toast.LENGTH_SHORT).show();
                } else {
                    UserAuthService.loginUser(SignInSignUpPAGE.this, username, password);
                    editor.putString("username", username);
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("lastPage", "MainActivity"); // Or whatever page they go to next
                    editor.apply();

                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly go to MainActivity (you can implement actual login logic here)
                Intent i = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(i);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Directly go to MainActivity (you can implement actual login logic here)
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            }
        });
    }

}