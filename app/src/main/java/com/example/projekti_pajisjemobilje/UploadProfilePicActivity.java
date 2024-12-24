package com.example.projekti_pajisjemobilje;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadProfilePicActivity extends AppCompatActivity {
private ProgressBar progressBar;
private ImageView imageViewUploadPic;
private FirebaseAuth authProfile;

private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Upload Profile Picture");

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBar= findViewById(R.id.progressBar);
        imageViewUploadPic = findViewById(R.id.imageView_profile_dp);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if(id==R.id.menu_update_email){
           Intent intent= new Intent(UploadProfilePicActivity.this, UpdateEmailActivity.class);
      startActivity(intent);
      finish();
       }else if(id==R.id.menu_settings){
           Toast.makeText(UploadProfilePicActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
          }else if(id==R.id.menu_change_password){
           Intent intent= new Intent(UploadProfilePicActivity.this, ChangePasswordActivity.class);
           startActivity(intent);
       }else if(id==R.id.menu_delete_profile){
           Intent intent= new Intent(UploadProfilePicActivity.this, DeleteProfileActivity.class);
           startActivity(intent);
       }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(UploadProfilePicActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UploadProfilePicActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }



}