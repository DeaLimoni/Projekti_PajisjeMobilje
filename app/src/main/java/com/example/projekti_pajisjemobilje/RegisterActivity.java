package com.example.projekti_pajisjemobilje;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
private static final String TAG="RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Regjistrohu");
        }

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        //radio button for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);


//obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDob = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString().trim();;
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString().trim();
                String textGender;

                //validate mobile number using matcher and pattern
                String mobileRegex ="0[0-9]{9}";
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                Matcher mobileMatcher = mobilePattern.matcher(textMobile);





                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDob)) { // Përdorni textDob këtu
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of Birth is required");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected = findViewById(radioGroupRegisterGender.getCheckedRadioButtonId());
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is required");
                } else if (textMobile.length() != 9) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile No. should be 9 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (!mobileMatcher.matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile No. is not valid");
                    editTextRegisterMobile.requestFocus();
                }

                else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirm is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please same password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.requestFocus();

                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDob, textGender, textMobile, textPwd);


                }
            }

            //register user using the credentials given
            private void registerUser(String textFullName, String textEmail, String textDob, String textGender, String textMobile, String textPwd) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                  UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                                  firebaseUser.updateProfile(profileChangeRequest);


                                    //enter user data into the firebase realtime database
                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob, textGender, textMobile);
                                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered user");

                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(RegisterActivity.this, "User registered successfully.Please verify your email", Toast.LENGTH_LONG).show();
                                                /*/open the user profile after successful registration
                                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish(); //to close register activity*/
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "User registered failed.Please try again!", Toast.LENGTH_LONG).show();

                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }


                                    });

                                } else{
                                    try{
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e){
                                        editTextRegisterPwd.requestFocus();
                                        editTextRegisterPwd.setError("Your password is too weak. Kindly use a mix of alphabets,numbers and special charcters.");
                                    }catch(FirebaseAuthInvalidCredentialsException e) {
                                        editTextRegisterPwd.requestFocus();
                                        editTextRegisterPwd.setError("Your email is invalid or already in use.Kindly re-enter.");
                                    }catch(FirebaseAuthUserCollisionException e){
                                        editTextRegisterPwd.requestFocus();
                                        editTextRegisterPwd.setError("User is already registered with this email.Use another email.");
                                    }catch (Exception e){
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }
}





