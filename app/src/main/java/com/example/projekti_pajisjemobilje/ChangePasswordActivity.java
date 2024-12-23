package com.example.projekti_pajisjemobilje;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
 private FirebaseUser authProfile;
 private EditText editTextpwdCurr, editTextPwdNew,editTextPwdConfirmNew;
   private TextView textViewAuthenticated;
   private Button buttonChangePwd, buttonReAuthenticate;
   private ProgressBar progressBar;
   private String userPwdCurr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Change Password");

        }
            editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
            editTextpwdCurr = findViewById(R.id.editText_change_pwd_current);
            editTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
            textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
            progressBar = findViewById(R.id.progressBar);
            buttonReAuthenticate= findViewById(R.id.button_change_pwd_authenticate);
            buttonChangePwd = findViewById(R.id.button_change_pwd);

            editTextPwdNew.setEnabled(false);
            editTextPwdConfirmNew.setEnabled(false);
            buttonChangePwd.setEnabled(false);
            

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
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_email){
            Intent intent= new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_settings){
            Toast.makeText(ChangePasswordActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }/*else if(id==R.id.menu_delete_profile){
           Intent intent= new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
           startActivity(intent);
       }*/else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(ChangePasswordActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(ChangePasswordActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}