package com.example.projekti_pajisjemobilje;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {
private FirebaseAuth authProfile;
private FirebaseUser firebaseUser;
private TextView textViewAuthenticated;
private EditText editTextUserPwd;
private ProgressBar progressBar;
private String userPwd;
private Button buttonReAuthenticate, buttonDeleteUser;
private static final String TAG ="DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Delete Your Profile");
        }
        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);


        buttonDeleteUser.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser.equals("")) {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong" + "User Detail's are not available at the moment",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }


    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextUserPwd.getText().toString();

                if (TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(DeleteProfileActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextUserPwd.setError("Please enter your current password to authenticate");
                    editTextUserPwd.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                editTextUserPwd.setEnabled(false);

                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                textViewAuthenticated.setText("You are authenticated/verified."+"You can delete profile and related data now!");
                                Toast.makeText(DeleteProfileActivity.this, "Password has been verified."+"" +"You can delete your profile now. Be careful, this action is irreversible!"
                                        , Toast.LENGTH_SHORT).show();
                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DeleteProfileActivity.this, R.color.dark_green));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            }else{
                                try{
                                    throw task.getException();
                                } catch (Exception e) {

                                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data?This action is irreversible!");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserData(firebaseUser);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
            }
        });

        AlertDialog alertDialog = builder.create();
alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
    }
});

        
        alertDialog.show();

    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    authProfile.signOut();
                    Toast.makeText(DeleteProfileActivity.this, "User has been deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData(FirebaseUser firebaseUser) {


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "User data successfully deleted from database");
                   deleteUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error deleting user data: " + e.getMessage());
                        Toast.makeText(DeleteProfileActivity.this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if(id==R.id.menu_refresh) {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }else if (id==R.id.menu_update_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if(id==R.id.menu_update_email){
            Intent intent= new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_settings){
            Toast.makeText(DeleteProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        }else if(id==R.id.menu_change_password){
            Intent intent= new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_delete_profile){
            Intent intent= new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(DeleteProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(DeleteProfileActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}