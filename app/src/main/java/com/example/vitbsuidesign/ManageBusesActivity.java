package com.example.vitbsuidesign;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitbsuidesign.databinding.ActivityManageBusesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageBusesActivity extends AppCompatActivity {

    private ActivityManageBusesBinding manageBusesBinding;
    DatabaseReference database;
    BusAdapter busAdapter;
    ArrayList<BusData> list;
    boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manageBusesBinding = ActivityManageBusesBinding.inflate(getLayoutInflater());
        setContentView(manageBusesBinding.getRoot());

        database = FirebaseDatabase.getInstance().getReference("Buses");

        manageBusesBinding.container.setVisibility(View.GONE);

        manageBusesBinding.rvBusList.setLayoutManager(new LinearLayoutManager(this));
        manageBusesBinding.rvBusList.setHasFixedSize(true);

        list = new ArrayList<>();
        busAdapter = new BusAdapter(this, list);
        manageBusesBinding.rvBusList.setAdapter(busAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for(DataSnapshot ds: snapshot.getChildren()) {

                    BusData busDataRV = ds.getValue(BusData.class);
                    list.add(busDataRV);

                }

                busAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        manageBusesBinding.fBtnAddBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBusInDatabase();
            }
        });
    }

    private void createNewBusInDatabase() {

        int busSize = list.size() + 1;
        String newBusID = "BusNo"+busSize+"#";

        BusData newBus = new BusData(newBusID,"NULL", "NULL", newBusID, 60, 0D, 0D);

        database.child(newBusID).setValue(newBus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ManageBusesActivity.this, newBusID + " is created", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}