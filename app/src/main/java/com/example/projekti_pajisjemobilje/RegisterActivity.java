package com.example.projekti_pajisjemobilje;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "You can register now",Toast.LENGTH_LONG).show();
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        //radio button for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();


        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View v) {

            int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);


//obtain the entered data
            String textFullName = editTextRegisterFullName.getText().toString();
            String textEmail = editTextRegisterEmail.getText().toString();
            String textDob = editTextRegisterDoB.getText().toString();
            String textMobile = editTextRegisterMobile.getText().toString();
            String textPwd = editTextRegisterPwd.getText().toString();
            String textConfirmed = editTextRegisterConfirmPwd.getText().toString();
            String textGender;
            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                editTextRegisterFullName.setError("Full Name is required");
                editTextRegisterFullName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
            editTextRegisterEmail.setError("Email is required");
                editTextRegisterEmail.requestFocus();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                editTextRegisterEmail.setError("Valid email is required");
                editTextRegisterEmail.requestFocus();
            }else if (TextUtils.isEmpty(textDob)) { // Përdorni textDob këtu
                Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                editTextRegisterDoB.setError("Date of Birth is required");
                editTextRegisterDoB.requestFocus();
            }else if(radioButtonRegisterGenderSelected.getCheckedRadioButtonId() == -1){
                Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                radioButtonRegisterGenderSelected.setError("gender is required");
                radioButtonRegisterGenderSelected.requestFocus();
        }else if(TextUtils.isEmpty(textMobile)){

            }

        });
    }
}