package com.example.projekti_pajisjemobilje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
private EditText editTextLoginEmail, editTextLoginPwd;
private ProgressBar progressBar;

private FirebaseAuth authProfile;
private static final String TAG="LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }


editTextLoginEmail = findViewById(R.id.editText_login_email);
editTextLoginPwd = findViewById(R.id.editText_login_pwd);
progressBar = findViewById(R.id.progressBar);

authProfile = FirebaseAuth.getInstance();

//show hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
       imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
       imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                   editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);

               }else {
                   editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
               }


           }
       });

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

    private void loginUser(String textEmail,String textPwd) {
    authProfile.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
         if(task.isSuccessful())  {
             Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
         }else{
             try{
                 throw task.getException();
             }catch(FirebaseAuthInvalidUserException e){
                 editTextLoginEmail.setError("User does not exist.Please register!");
                 editTextLoginEmail.requestFocus();
             }catch(FirebaseAuthInvalidCredentialsException e){
                 editTextLoginEmail.setError("Invalid crefentials. Kindly, check and re-enter!");
                 editTextLoginEmail.requestFocus();
             } catch(Exception e){
                 Log.e(TAG, e.getMessage());
                 Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
             }


             Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
         }
         progressBar.setVisibility(View.GONE);
        }
    });
    }
}