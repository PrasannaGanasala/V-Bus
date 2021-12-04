package com.example.vitbsuidesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.vitbsuidesign.databinding.ActivityAdminConsoleBinding;

public class AdminConsoleActivity extends AppCompatActivity {

    private ActivityAdminConsoleBinding adminConsoleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminConsoleBinding = ActivityAdminConsoleBinding.inflate(getLayoutInflater());
        setContentView(adminConsoleBinding.getRoot());

        adminConsoleBinding.tvRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationClicked();
            }
        });
        adminConsoleBinding.ivRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationClicked();
            }
        });

        adminConsoleBinding.tvQrGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrGeneratorClicked();
            }
        });
        adminConsoleBinding.ivQrGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrGeneratorClicked();
            }
        });

        adminConsoleBinding.tvManageBuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageBusesClicked();
            }
        });
        adminConsoleBinding.ivManageBuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManageBusesClicked();
            }
        });
    }

    private void ManageBusesClicked() {
        Toast.makeText(this, "Manage Buses Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminConsoleActivity.this, ManageBusesActivity.class));
    }

    private void QrGeneratorClicked() {
        startActivity(new Intent(AdminConsoleActivity.this, QR_GeneratorActivity.class));
    }

    private void RegistrationClicked() {
        startActivity(new Intent(AdminConsoleActivity.this, UserRegistrationActivity.class));
        Toast.makeText(this, "Registration Clicked", Toast.LENGTH_SHORT).show();

    }
}