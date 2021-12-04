package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.vitbsuidesign.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("VIT Bus App", MODE_PRIVATE);



        loginBinding.btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginClicked();
            }
        });
    }

    private void LoginClicked() {

        String email = loginBinding.etUsername.getText().toString().trim();
        String password = loginBinding.etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Password Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!loginBinding.rBtnStudent.isChecked() && !loginBinding.rBtnDriver.isChecked()) {
            if(!email.contains("admin")) {
                Toast.makeText(this, "Please choose the user type u want to login as...", Toast.LENGTH_SHORT).show();
            }
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            navigateActivity();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login Failed or User Not Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void navigateActivity() {

        if(loginBinding.rBtnDriver.isChecked()) {
            sp.edit().clear().apply();
            sp.edit().putString("User_Type", "driver").apply();
            Toast.makeText(this, "Driver Logged In Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, DriverConsoleActivity.class));
        }
        else if(loginBinding.rBtnStudent.isChecked()) {
            sp.edit().clear().apply();
            sp.edit().putString("User_Type", "student").apply();
            Toast.makeText(this, "Student Logged In Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, StudentConsoleActivity.class));
        }
        else {
            sp.edit().clear().apply();
            sp.edit().putString("User_Type", "admin").apply();
            Toast.makeText(this, "Admin Logged In Successfully", Toast.LENGTH_SHORT).show();
            //Navigate to admin home page
            startActivity(new Intent(LoginActivity.this, AdminConsoleActivity.class));
        }
    }
}