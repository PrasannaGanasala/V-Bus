package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vitbsuidesign.databinding.ActivityStudentConsoleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class StudentConsoleActivity extends AppCompatActivity {

    ActivityStudentConsoleBinding studentBinding;

    FirebaseAuth firebaseAuth;
    String userUID;
    String regNo= "";

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentBinding = ActivityStudentConsoleBinding.inflate(getLayoutInflater());
        setContentView(studentBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            userUID = currentUser.getUid();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Students").child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                regNo = Objects.requireNonNull(snapshot.child("regNo").getValue()).toString();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        studentBinding.tvQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });
        studentBinding.ivQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });

        studentBinding.trackBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentConsoleActivity.this, StudentMapsActivity.class));
            }
        });
        studentBinding.ivTrackBusLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentConsoleActivity.this, StudentMapsActivity.class));
            }
        });
    }

    private void scanQRCode() {

        IntentIntegrator intentIntegrator = new IntentIntegrator(StudentConsoleActivity.this);

        //Set prompt text
        intentIntegrator.setPrompt("For flash use volume up key");

        //Set beep
        intentIntegrator.setBeepEnabled(true);

        //Locked orientation
        intentIntegrator.setOrientationLocked(true);

        //Set capture activity
        intentIntegrator.setCaptureActivity(Capture.class);

        //Initiate scan
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Initialise intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //Check condition
        if(intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentConsoleActivity.this);

            if(intentResult.getContents().contains(regNo)) {
                //Set message
                builder.setMessage("Your attendance in Bus has been Marked! Please be seated!");
                String[] contentResult = intentResult.getContents().split("#");
                String busNo = contentResult[0];
                databaseReference.child("Buses").child(busNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int seatsAvailable = Objects.requireNonNull(snapshot).child("seatAvailability").getValue(Integer.class);
                        seatsAvailable = seatsAvailable - 1;
                        snapshot.child("seatAvailability").getRef().setValue(seatsAvailable);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
            }
            else {
                builder.setMessage("You are not registered!\nContact your Management Staff for further queries!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }


            //Show alert dialog
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), "OOPS...You did not scan anything!", Toast.LENGTH_SHORT).show();
        }
    }
}