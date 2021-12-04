package com.example.vitbsuidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class QR_GeneratorActivity extends AppCompatActivity {


    Button btnGenerateQR, btnShareQR, btnDownloadQR;
    ImageView ivOutputQRImage;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    private Spinner busSpinner;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;



    EditText et_QRDataInput;
    BitmapDrawable drawable;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        if(ActivityCompat.checkSelfPermission(QR_GeneratorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(QR_GeneratorActivity.this, "permission not given to write", Toast.LENGTH_SHORT).show();
        }
        if(ActivityCompat.checkSelfPermission(QR_GeneratorActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(QR_GeneratorActivity.this, "permission not given to read", Toast.LENGTH_SHORT).show();
        }

        initLayout();
        busSpinner = findViewById(R.id.busSpinnerList);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(QR_GeneratorActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerDataList);
        busSpinner.setAdapter(adapter);
        retrieveData();
    }

    private void initLayout() {

        et_QRDataInput = findViewById(R.id.et_QRDataInput);
        btnGenerateQR = findViewById(R.id.btn_generateQR);
        btnShareQR = findViewById(R.id.btn_shareQR);
        btnDownloadQR = findViewById(R.id.btn_downloadQR);
        ivOutputQRImage = findViewById(R.id.iv_outputQRImage);

        btnGenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String busNo = busSpinner.getSelectedItem().toString();
                et_QRDataInput.setText(busNo);

                databaseReference.child("Buses").child(busNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String studentList = "";
                        studentList = Objects.requireNonNull(snapshot.child("studentList").getValue(), "").toString();
                        et_QRDataInput.setText(studentList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String sText = et_QRDataInput.getText().toString().trim();
                MultiFormatWriter writer = new MultiFormatWriter();
                if(!sText.isEmpty()) {
                    try {
                        BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE, 350, 350);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        ivOutputQRImage.setImageBitmap(bitmap);
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(et_QRDataInput.getApplicationWindowToken(), 0);
                    }
                    catch (WriterException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(QR_GeneratorActivity.this, "Empty Box!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnShareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });

        btnDownloadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadQRImage();
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

    private void shareImage() {
        if(ivOutputQRImage.getDrawable()==null) {
            Toast.makeText(QR_GeneratorActivity.this, "No QR Code Generated", Toast.LENGTH_SHORT).show();
            return;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        drawable = (BitmapDrawable) ivOutputQRImage.getDrawable();
        bitmap = drawable.getBitmap();
        File file = new File(getExternalCacheDir()+"/"+"QR Picture"+".png");
        Intent intent = new Intent(Intent.ACTION_SEND);
        try {
            FileOutputStream outputStream= new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            e.printStackTrace();;
        }
        startActivity(Intent.createChooser(intent, "Share image Via:"));
    }

    private void downloadQRImage() {
        if(ivOutputQRImage.getDrawable()==null) {
            Toast.makeText(QR_GeneratorActivity.this, "NO QR Generated", Toast.LENGTH_SHORT).show();
            return;
        }

        drawable = (BitmapDrawable)  ivOutputQRImage.getDrawable();
        bitmap = drawable.getBitmap();

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "VIT BUS" + File.separator + "Admin" + File.separator + "Qr_Codes");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                OutputStream fos =  resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Toast.makeText(QR_GeneratorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}