package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.vitbsuidesign.databinding.ActivityUserRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserRegistrationActivity extends AppCompatActivity {

    private ActivityUserRegistrationBinding registrationBinding;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrationBinding = ActivityUserRegistrationBinding.inflate(getLayoutInflater());
        setContentView(registrationBinding.getRoot());
        getSupportActionBar().setTitle("Registration Form");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(UserRegistrationActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerDataList);
        registrationBinding.busSpinner.setAdapter(adapter);
        retrieveData();

        registrationBinding.btnUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = registrationBinding.etFullName.getText().toString().trim();
                String regNo = registrationBinding.etRegNo.getText().toString().trim();
                String email = registrationBinding.etEmail.getText().toString().trim();
                String password = registrationBinding.etPassword.getText().toString().trim();
                String cPassword = registrationBinding.etConfirmPassword.getText().toString().trim();
                String busNo;


                if(TextUtils.isEmpty(name)) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(regNo)) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Enter Registration No", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(cPassword)) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(registrationBinding.busSpinner == null) {
                    Toast.makeText(UserRegistrationActivity.this, "Please Choose a bus", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    busNo = registrationBinding.busSpinner.getSelectedItem().toString();
                }

                if(password.length()<7) {
                    Toast.makeText(UserRegistrationActivity.this, "Password length should be minimum 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(cPassword)) {
                    Toast.makeText(UserRegistrationActivity.this, "Entered Password don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(registrationBinding.rbtnDriver.isChecked()) {
                    registerDriver(name, regNo, email, password, busNo);

                } else if(registrationBinding.rbtnStudent.isChecked()) {
                    registerStudent(name, regNo, email, password, busNo);

                } else {
                    Toast.makeText(UserRegistrationActivity.this, "Please select a user type to register!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerStudent(String name, String regNo, String email, String password, String busNo) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    StudentData studentData = new StudentData(name, regNo, email, busNo, password);
                    firebaseDatabase.getReference().child("Students").child(Objects.requireNonNull(firebaseAuth.getUid()))
                            .setValue(studentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserRegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserRegistrationActivity.this, AdminConsoleActivity.class);
                            startActivity(intent);
                        }
                    });

                    firebaseDatabase.getReference("Buses").child(busNo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String studentList = "";
                            studentList = Objects.requireNonNull(snapshot.child("studentList").getValue(), "").toString();
                            studentList = studentList.concat(regNo + "#");

                            snapshot.getRef().child("studentList").setValue(studentList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Toast.makeText(UserRegistrationActivity.this, "We are unable to process your request! Sorry!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerDriver(String name, String regNo, String email, String password, String busNo) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    DriverData driverData = new DriverData(name, regNo, email, busNo, password);
                    firebaseDatabase.getReference("Drivers").child(Objects.requireNonNull(firebaseAuth.getUid()))
                            .setValue(driverData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserRegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserRegistrationActivity.this, AdminConsoleActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    Toast.makeText(UserRegistrationActivity.this, "We are unable to process your request! Sorry!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveData() {
        valueEventListener = databaseReference.child("Buses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinnerDataList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    spinnerDataList.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}