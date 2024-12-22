package com.example.projekti_pajisjemobilje;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

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
      progressBar = findViewById(R.id.progressBar);
      editTextUpdateName= findViewById(R.id.editText_update_profile_name);
      editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
      editTextUpdateMobile=findViewById(R.id.editText_update_profile_mobile);

      radioGroupUpdateGender =findViewById(R.id.radio_group_update_gender);
       authProfile= FirebaseAuth.getInstance();

        FirebaseUser firebaseUser= authProfile.getCurrentUser();

      showProfile(firebaseUser);

      /*/Update Email
        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADoB[]=textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);
                DatePickerDialog picker;
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        Button buttonUpdateProfile= findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);

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
}