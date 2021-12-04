package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DriverConsoleActivity extends AppCompatActivity {

    ImageView iv_shareLocation;
    TextView tv_shareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_console);
        iv_shareLocation = findViewById(R.id.iv_shareLocation);
        tv_shareLocation = findViewById(R.id.tv_shareLocation);
        iv_shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });
        tv_shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Initialise intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //Check condition
        if(intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverConsoleActivity.this);

            String[] contentResult = intentResult.getContents().split("#");
            String busNo = contentResult[0];

            builder.setMessage("Please Click 'OK' if you want to drive the bus" + busNo + "\nOr else Click 'Cancel'");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(DriverConsoleActivity.this, DriverMapsActivity.class);
                    intent.putExtra("BusNo", busNo);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            //Show alert dialog
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), "OOPS...You did not scan anything!", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanQRCode() {

        IntentIntegrator intentIntegrator = new IntentIntegrator(DriverConsoleActivity.this);

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
}