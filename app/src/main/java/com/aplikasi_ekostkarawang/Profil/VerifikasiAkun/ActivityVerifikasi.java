package com.aplikasi_ekostkarawang.Profil.VerifikasiAkun;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aplikasi_ekostkarawang.Lain.Notifikasi;
import com.aplikasi_ekostkarawang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityVerifikasi extends AppCompatActivity {
    Toolbar toolbarVerifikasiAkun;
    ImageView imgFotoKTP, imgFotoDiri;
    Button btnAmbilFotoKTP, btnAmbilFotoDiri, btnUnggahDataDiri;
    CheckBox CBUnggahDataDiri;

    SharedPreferences SP;
    ProgressDialog progressDialog;

    int pilihKamera;
    boolean fotoKTP, fotoDiri;
    ArrayList<Uri> imgDataDiriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        toolbarVerifikasiAkun   = findViewById(R.id.toolbarVerifikasiAkun);
        imgFotoKTP              = findViewById(R.id.imgFotoKTP);
        imgFotoDiri             = findViewById(R.id.imgFotoDiri);
        btnAmbilFotoKTP         = findViewById(R.id.btnAmbilFotoKTP);
        btnAmbilFotoDiri        = findViewById(R.id.btnAmbilFotoDiri);
        btnUnggahDataDiri       = findViewById(R.id.btnUnggahDataDiri);
        CBUnggahDataDiri        = findViewById(R.id.CBUnggahDataDiri);

        SP                      = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        progressDialog          = new ProgressDialog(this);
        fotoKTP                 = false;
        fotoDiri                = false;

        imgDataDiriList.clear();
        imgDataDiriList.add(null);
        imgDataDiriList.add(null);

        imgFotoKTP.setVisibility(View.GONE);
        imgFotoDiri.setVisibility(View.GONE);
        btnUnggahDataDiri.setEnabled(false);
        btnUnggahDataDiri.setBackgroundColor(ContextCompat.getColor(this, R.color.abuabu));

        unggahFotoKTP();
        unggahFotoDiri();
        checkBoxUnggahDataDiri();
        validasiUnggahDataDiri();
        prosesUnggahDataDiri();
        setSupportActionBar(toolbarVerifikasiAkun);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        PushDownAnim.setPushDownAnimTo(btnAmbilFotoKTP, btnAmbilFotoDiri, btnUnggahDataDiri);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void unggahFotoKTP(){
        btnAmbilFotoKTP.setOnClickListener(v -> permisionCamera(1));
    }

    public void unggahFotoDiri(){
        btnAmbilFotoDiri.setOnClickListener(v -> permisionCamera(2));
    }

    public void permisionCamera(int kamera){
        if(ContextCompat.checkSelfPermission(ActivityVerifikasi.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(ActivityVerifikasi.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(ActivityVerifikasi.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(ActivityVerifikasi.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityVerifikasi.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityVerifikasi.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                ActivityCompat.requestPermissions(ActivityVerifikasi.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, kamera);
            }
            else{
                ActivityCompat.requestPermissions(ActivityVerifikasi.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, kamera);
            }
        }
        else{
            pilihGambar(kamera);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED){
            pilihGambar(1);
        }
        else if(requestCode == 2 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED){
            pilihGambar(2);
        }
    }

    public void pilihGambar(int kamera){
        CropImage.activity()
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .setAutoZoomEnabled(false)
                .setBackgroundColor(Color.BLACK)
                .setMultiTouchEnabled(true)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setCropMenuCropButtonIcon(R.drawable.icon_check)
                .start(ActivityVerifikasi.this);
        pilihKamera = kamera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && pilihKamera == 1){
            imgFotoKTP.setImageURI(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri());
            imgDataDiriList.set(0, CropImage.getActivityResult(data).getUri());
            imgFotoKTP.setVisibility(View.VISIBLE);
            fotoKTP = true;

            validasiUnggahDataDiri();
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && pilihKamera == 2){
            imgFotoDiri.setImageURI(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri());
            imgDataDiriList.set(1, CropImage.getActivityResult(data).getUri());
            imgFotoDiri.setVisibility(View.VISIBLE);
            fotoDiri = true;

            validasiUnggahDataDiri();
        }
    }

    public void checkBoxUnggahDataDiri(){
        CBUnggahDataDiri.setOnCheckedChangeListener((buttonView, isChecked) -> validasiUnggahDataDiri());
    }

    public void validasiUnggahDataDiri(){
        if(fotoKTP && fotoDiri && CBUnggahDataDiri.isChecked()){
            btnUnggahDataDiri.setBackgroundColor(ContextCompat.getColor(ActivityVerifikasi.this, R.color.colorPrimary));
            btnUnggahDataDiri.setEnabled(true);
        }
        else{
            btnUnggahDataDiri.setBackgroundColor(ContextCompat.getColor(ActivityVerifikasi.this, R.color.abuabu));
            btnUnggahDataDiri.setEnabled(false);
        }
    }

    public void prosesUnggahDataDiri(){
        btnUnggahDataDiri.setOnClickListener(v -> {
            progressDialog.setMessage(getString(R.string.Mengunggah_Gambar_));
            progressDialog.setCancelable(false);
            progressDialog.show();

            String IDAkun                       = SP.getString("IDAkun", "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna");
            StorageReference storageReference   = FirebaseStorage.getInstance().getReference().child("Verifikasi").child(IDAkun);

            storageReference.child("Gambar_KTP.jpg").putFile(imgDataDiriList.get(0)).addOnSuccessListener(taskSnapshot -> storageReference.child("Gambar_Data_Diri.jpg")
                    .putFile(imgDataDiriList.get(1)).addOnSuccessListener(taskSnapshot1 -> {
                        databaseReference.child("Pemilik").child(IDAkun).child("Verifikasi").setValue("Menunggu");
                        databaseReference.child("Admin").child("Token").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                new Notifikasi(String.valueOf(snapshot.getValue()), getString(R.string.Verifikasi_Akun_Baru), SP.getString("EmailAkun", "") +
                                        getString(R.string.Telah_mengunggah_data_diri_), getApplicationContext(), ActivityVerifikasi.this).kirimNotifikasi();
                                FirebaseDatabase.getInstance().getReference().child("Notifikasi").child("Admin").child("Verifikasi").push().setValue(IDAkun);
                                progressDialog.dismiss();
                                finishAfterTransition();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }))
                    .addOnFailureListener(e -> progressDialog.dismiss());
        });
    }
}