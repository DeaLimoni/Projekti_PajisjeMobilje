package com.example.projekti_pajisjemobilje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UpdateEmailActivity extends AppCompatActivity {
private FirebaseAuth authProfile;
private FirebaseUser firebaseUser;
private ProgressBar progressBar;
private TextView textViewAuthenticated;
private String userOldEmail, userNewEmail, userPwd;
private Button buttonUpdateEmail;
private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Update Email");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
         editTextPwd = findViewById(R.id.editText_update_email_verify_password);
         editTextNewEmail = findViewById(R.id.editText_update_email_new);
         textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
         buttonUpdateEmail = findViewById(R.id.button_update_email);
         buttonUpdateEmail.setEnabled(false);
         editTextNewEmail.setEnabled(false);


         authProfile = FirebaseAuth.getInstance();
         firebaseUser = authProfile.getCurrentUser();


         userOldEmail = firebaseUser.getEmail();
         TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
         textViewOldEmail.setText(userOldEmail);

         if(firebaseUser.equals("")){
             Toast.makeText(UpdateEmailActivity.this,"Something went wrong! User's details not available.*",Toast.LENGTH_LONG).show();
         }else {
             reAuthenticate(firebaseUser);

         }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
   Button buttonVerifyUser= findViewById(R.id.button_authenticate_user);
   buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           userPwd= editTextPwd.getText().toString();
           if (TextUtils.isEmpty(userPwd)) {
               Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_LONG).show();
               editTextPwd.setError("Please enter your password for authentication");
               editTextPwd.requestFocus();
           }else{
               progressBar.setVisibility(View.VISIBLE);

               AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

               firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpdateEmailActivity.this,"Password has been verified."+"You can update email now",Toast.LENGTH_LONG).show();

                        textViewAuthenticated.setText("You are authenticated.You can update your email now.");


                        editTextNewEmail.setEnabled(true);
                        editTextPwd.setEnabled(false);
                        buttonVerifyUser.setEnabled(false);
                        buttonUpdateEmail.setEnabled(true);



                        buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                                R.color.dark_green));
                        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userNewEmail = editTextNewEmail.getText().toString();
                                if(TextUtils.isEmpty(userNewEmail)){
                                    Toast.makeText(UpdateEmailActivity.this,"New Email is required",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("Please enter new Email");
                                    editTextNewEmail.requestFocus();
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                    Toast.makeText(UpdateEmailActivity.this,"Please enter valid Email",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("Please provide valid Email");
                                    editTextNewEmail.requestFocus();
                                }else if(userOldEmail.matches(userNewEmail)){
                                    Toast.makeText(UpdateEmailActivity.this,"New Email cannot be same as old Email",Toast.LENGTH_SHORT).show();
                                    editTextNewEmail.setError("Please enter new Email");
                                    editTextNewEmail.requestFocus();
                                }else{
                                    progressBar.setVisibility(View.VISIBLE);
                                    updateEmail(firebaseUser);
                                }
                            }
                        });
                    }else{
                        try{
                            throw  task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateEmailActivity.this,e.getMessage() , Toast.LENGTH_SHORT).show();
                        }

                    }
                   }
               });
           }
       }
   });



    }

    private void updateEmail(FirebaseUser firebaseUser) {
     firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
      if(task.isComplete())  {
          firebaseUser.sendEmailVerification();
          Toast.makeText(UpdateEmailActivity.this, "Email has been updated.Please verify your new Email", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
      startActivity(intent);
      finish();
      }else{
          try {
              throw task.getException();
          } catch (Exception e) {
              Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
      }
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
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);
        }
         else if(id==R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_update_email){
            Intent intent= new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_settings){
           Toast.makeText(UpdateEmailActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
          }else if(id==R.id.menu_change_password){
           Intent intent= new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
           startActivity(intent);
           finish();
       }/*else if(id==R.id.menu_delete_profile){
           Intent intent= new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
           startActivity(intent);
       }*/else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(UpdateEmailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

}