package com.example.vitbsuidesign;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder>{

    Context context;
    ArrayList<BusData> list;


    public BusAdapter(Context context, ArrayList<BusData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {

        BusData bus = list.get(position);
        holder.busId.setText(bus.getBusID());
        holder.driverId.setText(bus.getDriverID());
        holder.driverName.setText(bus.getDriverName());
        holder.seatAvailability.setText(String.valueOf(bus.getSeatAvailability()));
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentBusID = list.get(holder.getAdapterPosition()).getBusID();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete " + list.get(holder.getAdapterPosition()).getBusID()+"?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Buses");
                        databaseReference.child(currentBusID).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(context, currentBusID +" DELETED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {

        TextView busId, driverId, driverName, seatAvailability;
        ImageView img_delete;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);

            busId = itemView.findViewById(R.id.tv_BusID);
            driverId = itemView.findViewById(R.id.tv_DriverID);
            driverName = itemView.findViewById(R.id.tv_DriverName);
            seatAvailability = itemView.findViewById(R.id.tv_SeatAvailability);
            img_delete = itemView.findViewById(R.id.rv_img_busDelete);
        }

    }
}
