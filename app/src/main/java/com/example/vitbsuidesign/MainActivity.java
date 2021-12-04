package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        sp = getSharedPreferences("VIT Bus App", MODE_PRIVATE);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(checkMyPermissions()) {
                    if(currentUser == null) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                    else {
                        navigateActivity();
                    }
                    timer.cancel();
                }
            }
        }, 3000, 1000);
    }

    private boolean checkMyPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                String permissionsDenied = "";
                for (String per : permissionsList) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied += "\n" + per;

                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //This block here means PERMANENTLY DENIED PERMISSION
                            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                    .setMessage("You have permanently denied the permission " + per + ", go to settings to enable the permission")
                                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            gotoApplicationSettings();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .setCancelable(false)
                                    .show();
                        } else {
                            //PERMISSION NOT GRANTED
                            checkMyPermissions();
                        }
                    }

                }
            }
        }
    }

    private void navigateActivity() {
       String userType = sp.getString("User_Type", null);

        if(userType != null){
            switch (userType) {
                case "admin":
                    startActivity(new Intent(MainActivity.this, AdminConsoleActivity.class));
                    break;
                case "student":
                    startActivity(new Intent(MainActivity.this, StudentConsoleActivity.class));
                    break;
                case "driver":
                    startActivity(new Intent(MainActivity.this, DriverConsoleActivity.class));
                    break;
            }

        }else {
            Toast.makeText(this, "navigation failed", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage("Some error Occurred! You have to Login to Continue.\nWould you like to Login")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(200);
                        }
                    });

            builder.show();
        }
    }

    private void gotoApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }



}