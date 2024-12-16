package com.example.projekti_pajisjemobilje;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
private EditText editTextLoginEmail, editTextLoginPwd;
private ProgressBar progressBar;

private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
getSupportActionBar().setTitle("Login");

editTextLoginEmail = findViewById(R.id.editText_login_email);
editTextLoginPwd = findViewById(R.id.editText_login_pwd);
progressBar = findViewById(R.id.progressBar);

authProfile = FirebaseAuth.getInstance();
// login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String textEmail = editTextLoginEmail.getText().toString();
             String textPwd = editTextLoginPwd.getText().toString();


             if(TextUtils.isEmpty(textEmail)) {
                 Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                 editTextLoginEmail.setError("Email is required");
                 editTextLoginEmail.requestFocus();
             } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                 Toast.makeText(LoginActivity.this,"Please re-enter your email", Toast.LENGTH_SHORT).show();
                 editTextLoginEmail.setError("Valid email is required");
                 editTextLoginEmail.requestFocus();
             }else if(TextUtils.isEmpty(textPwd)){
                 Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                 editTextLoginPwd.setError("Password is required");
                 editTextLoginPwd.requestFocus();
             }else{
                 progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail, textPwd);

             }


            }
        });


    }
}