package com.example.projekti_pajisjemobilje;

import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kontrolloni nëse përdoruesi është tashmë i kyçur dhe kaloni te UserProfileActivity nëse është
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish(); // Mbyll LoginActivity që të mos mund të ktheheni përsëri
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        // Show/Hide password
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(v -> {
            if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
            } else {
                editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
            }
        });

        // Login User
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            String textEmail = editTextLoginEmail.getText().toString();
            String textPwd = editTextLoginPwd.getText().toString();

            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                editTextLoginEmail.setError("Email is required");
                editTextLoginEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                editTextLoginEmail.setError("Valid email is required");
                editTextLoginEmail.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                editTextLoginPwd.setError("Password is required");
                editTextLoginPwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail, textPwd);
            }
        });
    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(LoginActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();

                FirebaseUser firebaseUser = authProfile.getCurrentUser();

                // Kontrollo nëse emaili është verifikuar
                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "You are logged in now!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                    finish();
                } else {

                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                }
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    editTextLoginEmail.setError("User does not exist. Please register!");
                    editTextLoginEmail.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    editTextLoginEmail.setError("Invalid credentials. Kindly, check and re-enter!");
                    editTextLoginEmail.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified!");
        builder.setMessage("Please verify your email now. You cannot log in without email verification.");

        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "You are already logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "You can log in now.", Toast.LENGTH_SHORT).show();
        }
    }
}
