
        package com.example.projekti_pajisjemobilje;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Set title for the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        // Initialize views
        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progressBar);

        //set onclicklistener
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);

            }
        });


        // Firebase initialization
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            // If the user is not logged in, show an error
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available", Toast.LENGTH_SHORT).show();
        } else {

            checkIfEmailVerified(firebaseUser);
            // If the user is logged in, show user profile data
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified!");
        builder.setMessage("Please verify your email now. You cannot log in without email verification next time.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // Get reference to the "User" node in Firebase
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail() ;
                    doB = readUserDetails.doB;
                    gender= readUserDetails.gender;
                    mobile= readUserDetails.mobile;

                    textViewWelcome.setText("Welcome" +" "+ fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                }else {
                    Toast.makeText(UserProfileActivity.this, "User data not available", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE); // Hide the progress bar after data is loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Show an error message if Firebase call fails
                Toast.makeText(UserProfileActivity.this, "Something went wrong while fetching data!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Hide the progress bar in case of error
            }
        });
    }

    //creating acitonbar menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id==R.id.menu_update_profile) {
           Intent intent= new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
startActivity(intent);
       }else if(id==R.id.menu_update_email){
           Intent intent= new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
      startActivity(intent);
       }/*else if(id==R.id.menu_settings){
           Toast.makeText(UserProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
          }else if(id==R.id.menu_change_password){
           Intent intent= new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
           startActivity(intent);
       }else if(id==R.id.menu_delete_profile){
           Intent intent= new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
           startActivity(intent);
       }*/else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(UserProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UserProfileActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}