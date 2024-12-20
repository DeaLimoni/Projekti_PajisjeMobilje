package com.example.projekti_pajisjemobilje;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Regjistrohu");
        }

        Toast.makeText(this, "You can register now", Toast.LENGTH_LONG).show();
        initializeUI();

        editTextRegisterDoB.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.button_register).setOnClickListener(v -> validateAndRegisterUser());
    }

    private void initializeUI() {
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        progressBar = findViewById(R.id.progressBar);
        radioGroupRegisterGender.clearCheck();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        picker = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) ->
                editTextRegisterDoB.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
        picker.show();
    }

    private void validateAndRegisterUser() {
        String textFullName = editTextRegisterFullName.getText().toString().trim();
        String textEmail = editTextRegisterEmail.getText().toString().trim();
        String textDob = editTextRegisterDoB.getText().toString().trim();
        String textMobile = editTextRegisterMobile.getText().toString().trim();
        String textPwd = editTextRegisterPwd.getText().toString();
        String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
        String textGender = "";

        int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
            textGender = radioButtonRegisterGenderSelected.getText().toString();
        }

        String mobileRegex = "^0[0-9]{8}$";
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)) {
            showError(editTextRegisterFullName, "Full Name is required");
        } else if (TextUtils.isEmpty(textEmail)) {
            showError(editTextRegisterEmail, "Email is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            showError(editTextRegisterEmail, "Valid email is required");
        } else if (TextUtils.isEmpty(textDob)) {
            showError(editTextRegisterDoB, "Date of Birth is required");
        } else if (TextUtils.isEmpty(textGender)) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(textMobile)) {
            showError(editTextRegisterMobile, "Mobile number is required");
        } else if (!mobileMatcher.matches()) {
            showError(editTextRegisterMobile, "Valid mobile number is required");
        } else if (TextUtils.isEmpty(textPwd)) {
            showError(editTextRegisterPwd, "Password is required");
        } else if (textPwd.length() < 6) {
            showError(editTextRegisterPwd, "Password too weak");
        } else if (TextUtils.isEmpty(textConfirmPwd)) {
            showError(editTextRegisterConfirmPwd, "Confirm your password");
        } else if (!textPwd.equals(textConfirmPwd)) {
            showError(editTextRegisterConfirmPwd, "Passwords do not match");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            registerUser(textFullName, textEmail, textDob, textGender, textMobile, textPwd);
        }
    }

    private void showError(EditText field, String errorMessage) {
        field.setError(errorMessage);
        field.requestFocus();
    }

    private void registerUser(String fullName, String email, String dob, String gender, String mobile, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    ReadWriteUserDetails writeuserDetails = new ReadWriteUserDetails(dob, gender, mobile);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    reference.child(firebaseUser.getUid()).setValue(writeuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser =auth.getCurrentUser();

                                firebaseUser.sendEmailVerification();

                                ReadWriteUserDetails writeuserDetails = new ReadWriteUserDetails(dob, gender, mobile);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                                reference.child(firebaseUser.getUid()).setValue(writeuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()) {
                                           firebaseUser.sendEmailVerification();
                                           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                           Toast.makeText(RegisterActivity.this, "Registration successful. Verify your email.", Toast.LENGTH_LONG).show();
                                           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                           startActivity(intent);
                                           finish();


                                       }else{  Toast.makeText(RegisterActivity.this,"User registered failed! Try again.", Toast.LENGTH_SHORT).show();
                                         progressBar.setVisibility(View.GONE);
                                       }


                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to save user data. Try again.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    handleRegistrationError(task.getException());
                }
            }
        });
    }


    private void handleRegistrationError(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            showError(editTextRegisterPwd, "Weak password. Use a mix of characters.");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            showError(editTextRegisterEmail, "Invalid email.");
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            showError(editTextRegisterEmail, "Email already in use.");
        } else {
            Log.e(TAG, exception.getMessage());
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }
}
