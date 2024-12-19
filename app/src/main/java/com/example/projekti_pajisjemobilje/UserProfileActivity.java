package com.example.projekti_pajisjemobilje;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
getSupportActionBar().setTitle("Home");

textViewWelcome = findViewById(R.id.textView_show_welcome);
textViewFullName = findViewById(R.id.textView_show_full_name);
textViewEmail = findViewById(R.id.textView_show_email);
textViewDoB = findViewById(R.id.textView_show_dob);
textViewMobile = findViewById(R.id.textView_show_mobile);
textViewGender = findViewById(R.id.textView_show_gender);
progressBar = findViewById(R.id.progressBar1);

authProfile = FirebaseAuth.getInstance();
FirebaseUser firebaseUser = authProfile.getCurrentUser();

if(firebaseUser == null){
    Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not availabe", Toast.LENGTH_SHORT).show();

}else{
    progressBar.setVisibility(TextView.VISIBLE);
    showUserProfile(firebaseUser);
}
    }
    private void showUserProfile(FirebaseUser firebaseUser){
        String userID = firebaseUser.getUid();

        //extracting user referenxe from database

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                  fullName= firebaseUser.getDisplayName();
                  email = firebaseUser.getEmail();
                  doB = readUserDetails.doB;
                  gender = readUserDetails.gender;
                  mobile = readUserDetails.mobile;


                  textViewWelcome.setText("Welcome,"+ fullName+"!");
                  textViewFullName.setText(fullName);
                  textViewEmail.setText(email);
                  textViewDoB.setText(doB);
                  textViewGender
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }
}
