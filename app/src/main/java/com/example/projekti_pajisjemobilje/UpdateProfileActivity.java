package com.example.projekti_pajisjemobilje;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
private EditText editTextUpdateName, editTextUpdateDoB,editTextUpdateMobile;
private RadioGroup radioGroupUpdateGender;
private RadioButton radioButtonUpdateGenderSelected;
private String textFullName, textDoB,textGender, textMobile;
private FirebaseAuth authProfile;
private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update Profile");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);
        authProfile = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

      //Update Email
        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADoB[] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);
                DatePickerDialog picker;
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);

            }

            private void updateProfile(FirebaseUser firebaseUser) {
                int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
                radioButtonUpdateGenderSelected = findViewById(selectedGenderID);
                String mobileRegex = "0[0-9]{8}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);
                // Validation checks
                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextUpdateName.setError("Full Name is required");
                    editTextUpdateName.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextUpdateDoB.setError("Date of birth is required");
                    editTextUpdateDoB.requestFocus();
                } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
                    Toast.makeText(UpdateProfileActivity.this, "Please select your gender ", Toast.LENGTH_LONG).show();
                    radioButtonUpdateGenderSelected.setError("Gender is required");
                    radioButtonUpdateGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("Mobile number is required");
                    editTextUpdateMobile.requestFocus();
                } else if (textMobile.length() != 9) {
                    Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("Mobile number is not valid");
                    editTextUpdateMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("Mobile number should be 9 digits");
                    editTextUpdateMobile.requestFocus();

                } else {
                    textGender = radioButtonUpdateGenderSelected.getText().toString();
                    textFullName = editTextUpdateName.getText().toString();
                     textDoB= editTextUpdateDoB.getText().toString();
                     textMobile= editTextUpdateMobile.getText().toString();

                     ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender, textMobile);
                      DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
                      String userID= firebaseUser.getUid();
                    progressBar.setVisibility(View.VISIBLE);

referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
            setDisplayName(textFullName).build();
            firebaseUser.updateProfile(profileUpdates);

            Toast.makeText(UpdateProfileActivity.this, "Update Sucessful!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            try {
                throw task.getException();
            } catch(Exception e){
                Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        progressBar.setVisibility(View.GONE);
    }
});
                }
            }
        });
    }


    private void showProfile(FirebaseUser firebaseUser) {
   String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
            if (readUserDetails!=null){
                textFullName = firebaseUser.getDisplayName();
                textDoB = readUserDetails.doB;
                textGender = readUserDetails.gender;
                textMobile = readUserDetails.mobile;

                editTextUpdateName.setText(textFullName);
                editTextUpdateDoB.setText(textDoB);
                editTextUpdateMobile.setText(textMobile);
                if(textGender.equals("Male")){
                    radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                }else{
                    radioButtonUpdateGenderSelected= findViewById(R.id.radio_female);
                }
                radioButtonUpdateGenderSelected.setChecked(true);
            }
            else{
                Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();

            }
            progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home) {
            NavUtils.navigateUpFromSameTask(UpdateProfileActivity.this);
        }
       else if(id==R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
         }else if(id==R.id.menu_update_email){
           Intent intent= new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
      startActivity(intent);
      finish();
       }else if(id==R.id.menu_settings){
           Toast.makeText(UpdateProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
          }else if(id==R.id.menu_change_password){
           Intent intent= new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
           startActivity(intent);
           finish();
       }else if(id==R.id.menu_delete_profile){
           Intent intent= new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
           startActivity(intent);
           finish();
       }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(UpdateProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UpdateProfileActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }




}