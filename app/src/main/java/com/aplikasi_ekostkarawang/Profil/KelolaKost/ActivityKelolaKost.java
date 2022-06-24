package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aplikasi_ekostkarawang.Lain.CircularReveal;
import com.aplikasi_ekostkarawang.Lain.CustomGridView;
import com.aplikasi_ekostkarawang.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ActivityKelolaKost extends AppCompatActivity implements OnMapReadyCallback {
    LinearLayout LLRootKelolaKost;
    TextView txtDataKostError;
    TextInputEditText txtDataNamaKost, txtDataDeskripsiKost, txtDataPeraturanKost, txtKostPerhari, txtKostPerbulan;
    TextInputEditText txtKostPertahun, txtNamaKamarKost, txtDataAlamatKost, txtFasilitasSekitar, txtDekatDengan;
    Spinner spinnerDataMayoritasPenghuni;
    RadioButton RBNamaKamarTerisi, RBNamaKamarKosong;
    Button btnTambahNamaKamar, btnTambahFasilitasSekitar, btnTambahDekatDengan, btnSimpanDataKost;
    CustomGridView GVNamaKamarKost, GVDataFotoKost, GVDataVideoKost, GVFasilitasSekitar, GVDekatDengan;
    AutoCompleteTextView txtDataNamaKecamatan, txtDataNamaDesa;
    CheckBox CBFasilitasWifi, CBFasilitasCCTV, CBFasilitasJemuran, CBFasilitasLaundry, CBFasilitasParkirMotor, CBFasilitasParkirMobil;
    CheckBox CBFasilitasSumberAirTanah, CBFasilitasSumberAirPam, CBFasilitasRuangBersama, CBFasilitasDapur, CBFasilitasTelevisi, CBFasilitasKulkas;
    CheckBox CBFasilitasKeamanan, CBFasilitasDispenser, CBFasilitasKamarMandiDalam, CBFasilitasKamarMandiLuar, CBFasilitasToiletJongkok, CBFasilitasToiletDuduk;
    CheckBox CBFasilitasKamarMandiBak, CBFasilitasKamarMandiShower,CBFasilitasKamarAC, CBFasilitasTermasukListrik, CBFasilitasLemari, CBFasilitasTempatTidur;

    SupportMapFragment SMP;
    ScrollView SVDataKost;

    GoogleMap GM;
    Location lokasi;
    SharedPreferences SP;
    MarkerOptions markerOptions;
    FusedLocationProviderClient FLPC;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    ArrayList<String> listMayoritasPenghuni = new ArrayList<>();
    ArrayList<String> listNamaKamarKost     = new ArrayList<>();
    ArrayList<Uri> listFotoKost             = new ArrayList<>();
    ArrayList<Uri> listVideoKost            = new ArrayList<>();
    ArrayList<String> listKecamatan         = new ArrayList<>();
    ArrayList<String> listDesa              = new ArrayList<>();
    ArrayList<String> listFasilitasBersama  = new ArrayList<>();
    ArrayList<String> listFasilitasSekitar  = new ArrayList<>();
    ArrayList<String> listDekatDengan       = new ArrayList<>();
    ArrayList<String> listUpdateFotoKost    = new ArrayList<>();
    ArrayList<String> listUpdateVideoKost   = new ArrayList<>();

    boolean Foto, JumlahFoto, JumlahVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        setContentView(R.layout.activity_kelola_kost);

        LLRootKelolaKost                = findViewById(R.id.LLRootKelolaKost);
        txtDataKostError                = findViewById(R.id.txtDataKostError);
        SVDataKost                      = findViewById(R.id.SVDataKost);
        txtDataNamaKost                 = findViewById(R.id.txtDataNamaKost);
        txtDataDeskripsiKost            = findViewById(R.id.txtDataDeskripsiKost);
        txtDataPeraturanKost            = findViewById(R.id.txtDataPeraturanKost);
        txtKostPerhari                  = findViewById(R.id.txtKostPerhari);
        txtKostPerbulan                 = findViewById(R.id.txtKostPerbulan);
        txtKostPertahun                 = findViewById(R.id.txtKostPertahun);
        txtNamaKamarKost                = findViewById(R.id.txtNamaKamarKost);
        txtDataAlamatKost               = findViewById(R.id.txtDataAlamatKost);
        txtFasilitasSekitar             = findViewById(R.id.txtFasilitasSekitar);
        txtDekatDengan                  = findViewById(R.id.txtDekatDengan);
        spinnerDataMayoritasPenghuni    = findViewById(R.id.spinnerDataMayoritasPenghuni);
        RBNamaKamarTerisi               = findViewById(R.id.RBNamaKamarTerisi);
        RBNamaKamarKosong               = findViewById(R.id.RBNamaKamarKosong);
        btnTambahNamaKamar              = findViewById(R.id.btnTambahNamaKamar);
        btnTambahFasilitasSekitar       = findViewById(R.id.btnTambahFasilitasSekitar);
        btnTambahDekatDengan            = findViewById(R.id.btnTambahDekatDengan);
        btnSimpanDataKost               = findViewById(R.id.btnSimpanDataKost);
        GVNamaKamarKost                 = findViewById(R.id.GVNamaKamarKost);
        GVDataFotoKost                  = findViewById(R.id.GVDataFotoKost);
        GVDataVideoKost                 = findViewById(R.id.GVDataVideoKost);
        GVFasilitasSekitar              = findViewById(R.id.GVFasilitasSekitar);
        GVDekatDengan                   = findViewById(R.id.GVDekatDengan);
        txtDataNamaKecamatan            = findViewById(R.id.txtDataNamaKecamatan);
        txtDataNamaDesa                 = findViewById(R.id.txtDataNamaDesa);
        CBFasilitasWifi                 = findViewById(R.id.CBFasilitasWifi);
        CBFasilitasCCTV                 = findViewById(R.id.CBFasilitasCCTV);
        CBFasilitasJemuran              = findViewById(R.id.CBFasilitasJemuran);
        CBFasilitasLaundry              = findViewById(R.id.CBFasilitasLaundry);
        CBFasilitasParkirMotor          = findViewById(R.id.CBFasilitasParkirMotor);
        CBFasilitasParkirMobil          = findViewById(R.id.CBFasilitasParkirMobil);
        CBFasilitasSumberAirTanah       = findViewById(R.id.CBFasilitasSumberAirTanah);
        CBFasilitasSumberAirPam         = findViewById(R.id.CBFasilitasSumberAirPam);
        CBFasilitasRuangBersama         = findViewById(R.id.CBFasilitasRuangBersama);
        CBFasilitasDapur                = findViewById(R.id.CBFasilitasDapur);
        CBFasilitasTelevisi             = findViewById(R.id.CBFasilitasTelevisi);
        CBFasilitasKulkas               = findViewById(R.id.CBFasilitasKulkas);
        CBFasilitasKeamanan             = findViewById(R.id.CBFasilitasKeamanan);
        CBFasilitasDispenser            = findViewById(R.id.CBFasilitasDispenser);
        CBFasilitasKamarMandiDalam      = findViewById(R.id.CBFasilitasKamarMandiDalam);
        CBFasilitasKamarMandiLuar       = findViewById(R.id.CBFasilitasKamarMandiLuar);
        CBFasilitasToiletJongkok        = findViewById(R.id.CBFasilitasToiletJongkok);
        CBFasilitasToiletDuduk          = findViewById(R.id.CBFasilitasToiletDuduk);
        CBFasilitasKamarMandiBak        = findViewById(R.id.CBFasilitasKamarMandiBak);
        CBFasilitasKamarMandiShower     = findViewById(R.id.CBFasilitasKamarMandiShower);
        CBFasilitasKamarAC              = findViewById(R.id.CBFasilitasKamarAC);
        CBFasilitasTermasukListrik      = findViewById(R.id.CBFasilitasTermasukListrik);
        CBFasilitasLemari               = findViewById(R.id.CBFasilitasLemari);
        CBFasilitasTempatTidur          = findViewById(R.id.CBFasilitasTempatTidur);

        SMP                             = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentDataLokasi);
        FLPC                            = LocationServices.getFusedLocationProviderClient(this);
        SP                              = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        progressDialog                  = new ProgressDialog(this);

        listMayoritasPenghuni.add(getString(R.string.umum));
        listMayoritasPenghuni.add(getString(R.string.pria));
        listMayoritasPenghuni.add(getString(R.string.wanita));

        spinnerDataMayoritasPenghuni.setPadding(10, 0, 0, 0);
        spinnerDataMayoritasPenghuni.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listMayoritasPenghuni));

        hargaKost(txtKostPerhari);
        hargaKost(txtKostPerbulan);
        hargaKost(txtKostPertahun);

        tambahNamaKamarKost();
        tambahFotoVideoKost();
        mengambilDataKecamatan();
        mengambilLokasiTerakhir();
        validasiFasilitasBersama();
        tambahFasilitasSekitarDekatDengan();
        validasiSimpanUpdateKelolaDataKost();
        mengecekMengambilKelolaDataKostSudahAda();
        CircularReveal.circularRevealOpenActivity(savedInstanceState, LLRootKelolaKost, this);
        PushDownAnim.setPushDownAnimTo(btnTambahNamaKamar, btnTambahFasilitasSekitar, btnTambahDekatDengan, btnSimpanDataKost);
    }

    public void hargaKost(TextInputEditText txtKostPer){
        txtKostPer.addTextChangedListener(new TextWatcher() {
            String hargaKost = Objects.requireNonNull(txtKostPer.getText()).toString().trim();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(hargaKost)){
                    txtKostPer.removeTextChangedListener(this);
                    String ganti = s.toString().replaceAll("[Rp. ]", "");

                    if(!ganti.isEmpty()){
                        NumberFormat NF = NumberFormat.getCurrencyInstance(new Locale("IND", "ID"));
                        String[] split  = NF.format(Double.parseDouble(ganti)).split(",");
                        hargaKost       = split[0].substring(0, 2) + ". " + split[0].substring(2);
                    }
                    else{
                        hargaKost       = "";
                    }

                    txtKostPer.setText(hargaKost);
                    txtKostPer.setSelection(txtKostPer.length());
                    txtKostPer.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void tambahNamaKamarKost(){
        GVNamaKamarKost.setExpanded(true);
        btnTambahNamaKamar.setEnabled(false);
        btnTambahNamaKamar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.abuabu)));

        btnTambahNamaKamar.setOnClickListener(v -> {
            listNamaKamarKost.add(txtNamaKamarKost.getText().toString() + Html.fromHtml("&#8658;") + (RBNamaKamarTerisi.isChecked() ? getString(R.string.terisi) : getString(R.string.kosong)));
            GVNamaKamarKost.setAdapter(new AdapterFasilitas(this, listNamaKamarKost));
            GVNamaKamarKost.setVisibility(View.VISIBLE);
            txtNamaKamarKost.getText().clear();
        });
        validasiButtonTambah(txtNamaKamarKost, btnTambahNamaKamar);
    }

    public void validasiButtonTambah(TextInputEditText textInputEditText, Button button){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            @SuppressWarnings("ConstantConditions")
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textInputEditText.getText().toString().isEmpty()){
                    button.setEnabled(false);
                    button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityKelolaKost.this, R.color.abuabu)));
                }
                else{
                    button.setEnabled(true);
                    button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityKelolaKost.this, R.color.colorPrimary)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void tambahFotoVideoKost(){
        listFotoKost.add(null);
        listVideoKost.add(null);
        GVDataFotoKost.setExpanded(true);
        GVDataVideoKost.setExpanded(true);
        GVDataFotoKost.setAdapter(new AdapterFotoVideo(this, listFotoKost, "Foto"));
        GVDataVideoKost.setAdapter(new AdapterFotoVideo(this, listVideoKost, "Video"));

        GVDataFotoKost.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                pilihFotoVideo("image/*", 1);
                Foto = true;
            }
        });

        GVDataVideoKost.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                pilihFotoVideo("video/*", 2);
                Foto = false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void pilihFotoVideo(String Type, int requestCode){
        if(ContextCompat.checkSelfPermission(ActivityKelolaKost.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(ActivityKelolaKost.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(ActivityKelolaKost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(ActivityKelolaKost.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityKelolaKost.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityKelolaKost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                ActivityCompat.requestPermissions(ActivityKelolaKost.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
            }
            else{
                ActivityCompat.requestPermissions(ActivityKelolaKost.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
            }
        }
        else{
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true).setType(Type), requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 3 && grantResults.length > 0 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED){
            if(Foto){
                pilihFotoVideo("image/*", 1);
            }
            else{
                pilihFotoVideo("video/*", 2);
            }
        }
        else if(requestCode == 4 && grantResults.length > 0){
            mengambilLokasiTerakhir();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            tampilFotoVideo(data, listFotoKost, GVDataFotoKost, "Foto");
        }
        else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            tampilFotoVideo(data, listVideoKost, GVDataVideoKost, "Video");
        }
        else if(requestCode == 5 && resultCode == RESULT_OK && data != null){
            LatLng latLng   = new LatLng(SP.getFloat("Latitude", 0), SP.getFloat("Longitude", 0));
            markerOptions   = new MarkerOptions().position(latLng).title(getString(R.string.lokasi_kost));

            GM.clear();
            GM.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            GM.addMarker(markerOptions);
        }
    }

    public void tampilFotoVideo(Intent data, ArrayList<Uri> listFotoVideo, CustomGridView GV, String jenis){
        if(data.getClipData() != null){
            for(int i = 0; i < data.getClipData().getItemCount(); i++){
                listFotoVideo.add(data.getClipData().getItemAt(i).getUri());
            }
        }
        else{
            listFotoVideo.add(data.getData());
        }

        GV.setAdapter(new AdapterFotoVideo(this, listFotoVideo, jenis));
    }

    public void mengambilDataKecamatan(){
        txtDataNamaKecamatan.setThreshold(1);
        txtDataNamaKecamatan.setAdapter(new ArrayAdapter<>(ActivityKelolaKost.this, android.R.layout.simple_list_item_1, listKecamatan));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wilayah").child("Kecamatan");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listKecamatan.add(String.valueOf(dataSnapshot.child("nama_kecamatan").getValue()));
                    Collections.sort(listKecamatan);
                }
                cariIDKecamatan();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void cariIDKecamatan(){
        txtDataNamaKecamatan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Wilayah").child("Kecamatan");
                databaseReference.orderByChild("nama_kecamatan").equalTo(txtDataNamaKecamatan.getText().toString())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        listDesa.clear();
                                        mengambilDataDesa(String.valueOf(dataSnapshot.child("id").getValue()));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
            }
        });
    }

    public void mengambilDataDesa(String IDKecamatan){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Wilayah").child("Desa");
        databaseReference.orderByChild("id_kecamatan").equalTo(IDKecamatan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listDesa.add(String.valueOf(dataSnapshot.child("nama_desa").getValue()));
                    Collections.sort(listDesa);
                }

                txtDataNamaDesa.setThreshold(1);
                txtDataNamaDesa.getText().clear();
                txtDataNamaDesa.setAdapter(new ArrayAdapter<>(ActivityKelolaKost.this, android.R.layout.simple_list_item_1, listDesa));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengambilLokasiTerakhir(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 4);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
            }
        }
        else{
            FLPC.getLastLocation().addOnSuccessListener(location -> {
                lokasi = location;
                SMP.getMapAsync(ActivityKelolaKost.this);
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GM              = googleMap;
        LatLng latLng   = new LatLng(lokasi.getLatitude(), lokasi.getLongitude());
        markerOptions   = new MarkerOptions().position(latLng).title(getString(R.string.lokasi_kost));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        googleMap.addMarker(markerOptions);
        markerOptions.draggable(false);

        googleMap.setOnMapClickListener(latLng1 -> {
            SP.edit().putFloat("Latitude", (float)markerOptions.getPosition().latitude).apply();
            SP.edit().putFloat("Longitude", (float)markerOptions.getPosition().longitude).apply();
            startActivityForResult(new Intent(ActivityKelolaKost.this, ActivityMap.class), 5);
        });
    }

    public void validasiFasilitasBersama(){
        simpanHapusFasilitasBersama(CBFasilitasWifi, "Wifi");
        simpanHapusFasilitasBersama(CBFasilitasCCTV, "CCTV");
        simpanHapusFasilitasBersama(CBFasilitasJemuran, "Jemuran");
        simpanHapusFasilitasBersama(CBFasilitasLaundry, "Laundry");
        simpanHapusFasilitasBersama(CBFasilitasParkirMotor, "Parkir Motor");
        simpanHapusFasilitasBersama(CBFasilitasParkirMobil, "Parkir Mobil");
        simpanHapusFasilitasBersama(CBFasilitasSumberAirTanah, "Sumber Air Tanah");
        simpanHapusFasilitasBersama(CBFasilitasSumberAirPam, "Sumber Air PAM");
        simpanHapusFasilitasBersama(CBFasilitasRuangBersama, "Ruang Bersama");
        simpanHapusFasilitasBersama(CBFasilitasDapur, "Dapur atau Pantry");
        simpanHapusFasilitasBersama(CBFasilitasTelevisi, "Televisi");
        simpanHapusFasilitasBersama(CBFasilitasKulkas, "Kulkas");
        simpanHapusFasilitasBersama(CBFasilitasKeamanan, "Keamanan");
        simpanHapusFasilitasBersama(CBFasilitasDispenser, "Dispenser");
        simpanHapusFasilitasBersama(CBFasilitasKamarMandiDalam, "Kamar Mandi Dalam");
        simpanHapusFasilitasBersama(CBFasilitasKamarMandiLuar, "Kamar Mandi Luar");
        simpanHapusFasilitasBersama(CBFasilitasToiletJongkok, "Toilet Jongkok");
        simpanHapusFasilitasBersama(CBFasilitasToiletDuduk, "Toilet Duduk");
        simpanHapusFasilitasBersama(CBFasilitasKamarMandiBak, "Kamar Mandi Bak");
        simpanHapusFasilitasBersama(CBFasilitasKamarMandiShower, "Shower");
        simpanHapusFasilitasBersama(CBFasilitasKamarAC, "Kamar AC");
        simpanHapusFasilitasBersama(CBFasilitasTermasukListrik, "Termasuk Listrik");
        simpanHapusFasilitasBersama(CBFasilitasLemari, "Lemari");
        simpanHapusFasilitasBersama(CBFasilitasTempatTidur, "Tempat Tidur");
    }

    public void simpanHapusFasilitasBersama(CheckBox checkBox, String fasilitasBersama){
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                listFasilitasBersama.add(fasilitasBersama);
            }
            else{
                listFasilitasBersama.remove(fasilitasBersama);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void tambahFasilitasSekitarDekatDengan(){
        GVDekatDengan.setExpanded(true);
        GVFasilitasSekitar.setExpanded(true);
        btnTambahDekatDengan.setEnabled(false);
        btnTambahFasilitasSekitar.setEnabled(false);
        btnTambahDekatDengan.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.abuabu)));
        btnTambahFasilitasSekitar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.abuabu)));

        btnTambahDekatDengan.setOnClickListener(v -> {
            listDekatDengan.add(txtDekatDengan.getText().toString());
            GVDekatDengan.setAdapter(new AdapterFasilitas(this, listDekatDengan));
            txtDekatDengan.getText().clear();
        });

        btnTambahFasilitasSekitar.setOnClickListener(v -> {
            listFasilitasSekitar.add(txtFasilitasSekitar.getText().toString());
            GVFasilitasSekitar.setAdapter(new AdapterFasilitas(this, listFasilitasSekitar));
            txtFasilitasSekitar.getText().clear();
        });

        validasiButtonTambah(txtDekatDengan, btnTambahDekatDengan);
        validasiButtonTambah(txtFasilitasSekitar, btnTambahFasilitasSekitar);
    }

    @SuppressWarnings("ConstantConditions")
    public void validasiSimpanUpdateKelolaDataKost(){
        btnSimpanDataKost.setOnClickListener(v -> {
            if(txtDataNamaKost.getText().toString().trim().isEmpty() || txtDataDeskripsiKost.getText().toString().trim().isEmpty() ||
                    txtDataPeraturanKost.getText().toString().trim().isEmpty()){

                visibilityError(getString(R.string.Lengkapi_Data_Umum_Kost));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.view_data_umum_kost).getTop());
            }
            else if(txtKostPerhari.getText().toString().trim().isEmpty() && txtKostPerbulan.getText().toString().trim().isEmpty() &&
                    txtKostPertahun.getText().toString().trim().isEmpty()){

                visibilityError(getString(R.string.Isi_salah_satu_harga_kost_));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.txtKostPerhari).getTop());
            }
            else if(listNamaKamarKost.size() == 0){
                visibilityError(getString(R.string.Nama_kamar_kost_belum_di_buat_));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.txtNamaKamarKost).getTop());
            }
            else if(listFotoKost.size() == 1 && listFotoKost.get(0) == null && listVideoKost.size() == 1 && listVideoKost.get(0) == null){
                visibilityError(getString(R.string.Tambahkan_Foto_atau_Video_Kost));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.view_foto_video_kost).getTop());
            }
            else if(txtDataNamaKecamatan.getText().toString().trim().isEmpty() || txtDataNamaDesa.getText().toString().trim().isEmpty() ||
                    txtDataAlamatKost.getText().toString().trim().isEmpty()){

                visibilityError(getString(R.string.Lengkapi_Data_Lokasi_Kost));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.view_lokasi_kost).getTop());
            }
            else if(listFasilitasBersama.size() == 0){
                visibilityError(getString(R.string.Fasilitas_Bersama_Tidak_Ada));
                SVDataKost.smoothScrollTo(0, findViewById(R.id.view_fasilitas_bersama).getTop());
            }
            else{
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                progressDialog.setMessage(getString(R.string.Menyimpan_Data_Kost));
                progressDialog.setCancelable(false);
                progressDialog.show();

                simpanUpdateDataKost();
            }
        });
    }

    public void visibilityError(String error){
        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                txtDataKostError.setVisibility(View.VISIBLE);
                txtDataKostError.setText(error);
            }

            @Override
            public void onFinish() {
                txtDataKostError.setVisibility(View.GONE);
            }
        }.start();
    }

    @SuppressWarnings("ConstantConditions")
    public void simpanUpdateDataKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", ""));
        databaseReference.child("Pemilik").setValue(SP.getString("EmailAkun", ""));

        Map<String, Object> mapDataUmumKost = new HashMap<>();
        mapDataUmumKost.put("Nama_Kost", txtDataNamaKost.getText().toString());
        mapDataUmumKost.put("Deskripsi_Kost", txtDataDeskripsiKost.getText().toString());
        mapDataUmumKost.put("Peraturan_Kost", txtDataPeraturanKost.getText().toString());
        mapDataUmumKost.put("Mayoritas_Penghuni", spinnerDataMayoritasPenghuni.getSelectedItem().toString());

        databaseReference.child("Data_Umum_Kost").setValue(mapDataUmumKost);
        databaseReference.child("Data_Umum_Kost").child("Harga_Kost").child("Hari").setValue(txtKostPerhari.getText().toString());
        databaseReference.child("Data_Umum_Kost").child("Harga_Kost").child("Bulan").setValue(txtKostPerbulan.getText().toString());
        databaseReference.child("Data_Umum_Kost").child("Harga_Kost").child("Tahun").setValue(txtKostPertahun.getText().toString());
        databaseReference.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Total_Kamar").setValue(String.valueOf(listNamaKamarKost.size()));
        databaseReference.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Sisa_Kamar").setValue(sisaKamar());

        databaseReference.child("Data_Umum_Kost").child("Nama_Kamar").removeValue();
        for(int i = 0; i < listNamaKamarKost.size(); i++){
            databaseReference.child("Data_Umum_Kost").child("Nama_Kamar").child(String.valueOf(i + 1)).setValue(listNamaKamarKost.get(i));
        }

        databaseReference.child("Lokasi_Kost").child("Kecamatan").setValue(txtDataNamaKecamatan.getText().toString());
        databaseReference.child("Lokasi_Kost").child("Desa").setValue(txtDataNamaDesa.getText().toString());
        databaseReference.child("Lokasi_Kost").child("Alamat_Kost").setValue(txtDataAlamatKost.getText().toString());
        databaseReference.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").setValue(markerOptions.getPosition().latitude);
        databaseReference.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").setValue(markerOptions.getPosition().longitude);

        databaseReference.child("Fasilitas_Bersama").removeValue();
        for(int i = 0; i < listFasilitasBersama.size(); i++){
            databaseReference.child("Fasilitas_Bersama").child(String.valueOf(i + 1)).setValue(listFasilitasBersama.get(i));
        }

        databaseReference.child("Fasilitas_Sekitar").removeValue();
        for(int i = 0; i < listFasilitasSekitar.size(); i++){
            databaseReference.child("Fasilitas_Sekitar").child(String.valueOf(i + 1)).setValue(listFasilitasSekitar.get(i));
        }

        databaseReference.child("Dekat_Dengan").removeValue();
        for(int i = 0; i < listDekatDengan.size(); i++){
            databaseReference.child("Dekat_Dengan").child(String.valueOf(i + 1)).setValue(listDekatDengan.get(i));
        }

        cekFotoVideoKost();
    }

    public String sisaKamar(){
        int sisaKamar = 0;
        for(int i = 0; i < listNamaKamarKost.size(); i++){
            if(listNamaKamarKost.get(i).contains(getString(R.string.kosong))){
                sisaKamar++;
            }
        }
        return String.valueOf(sisaKamar);
    }

    public void cekFotoVideoKost(){
        FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", "")).child("Foto_Video_Kost").removeValue();

        for(int i = listFotoKost.size() - 1; i > 0; i--){
            if(listFotoKost.get(i).toString().startsWith("https://firebasestorage.googleapis.com/v0/b/e-kost-karawang.appspot.com")){
                FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", "")).child("Foto_Video_Kost").child("Foto")
                        .push().setValue(listFotoKost.get(i).toString());
                listFotoKost.remove(i);
            }
        }

        for(int i = listVideoKost.size() - 1; i > 0; i--){
            if(listVideoKost.get(i).toString().startsWith("https://firebasestorage.googleapis.com/v0/b/e-kost-karawang.appspot.com")){
                FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", "")).child("Foto_Video_Kost").child("Video")
                        .push().setValue(listVideoKost.get(i).toString());
                listVideoKost.remove(i);
            }
        }

        if(listFotoKost.size() == 1 && listFotoKost.get(0) == null && listVideoKost.size() == 1 && listVideoKost.get(0) == null){
            progressDialog.dismiss();
            CircularReveal.circularRevealCloseActivity(LLRootKelolaKost, this);
        }
        else{
            if(listFotoKost.size() == 1 && listFotoKost.get(0) == null){
                mengunggahVideo();
            }
            else{
                mengunggahFoto();
            }
        }
    }

    public void mengunggahFoto(){
        storageReference = FirebaseStorage.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", ""));

        for(int i = 1; i < listFotoKost.size(); i++){
            int I = i;
            StorageReference Nama_Foto = storageReference.child("Foto_Kost").child(listFotoKost.get(i).getLastPathSegment() + ".jpg");
            Nama_Foto.putFile(listFotoKost.get(i)).addOnProgressListener(snapshot ->
                    progressDialog.setMessage(getString(R.string.Mengunggah_Foto) + " " + I + " " + getString(R.string.dari) + " " + (listFotoKost.size() - 1) + " (" +
                    ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount()) + "%)")
            ).addOnSuccessListener(taskSnapshot -> Nama_Foto.getDownloadUrl().addOnSuccessListener(uri -> {
               databaseReference.child("Foto_Video_Kost").child("Foto").push().setValue(uri.toString());

               if(I == (listFotoKost.size() - 1)){
                   if(listVideoKost.size() == 1 && listVideoKost.get(0) == null){
                       progressDialog.dismiss();
                       CircularReveal.circularRevealCloseActivity(LLRootKelolaKost, this);
                   }
                   else{
                       mengunggahVideo();
                   }
               }
            }));
        }
    }

    public void mengunggahVideo(){
        storageReference = FirebaseStorage.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", ""));

        for(int i = 1; i < listVideoKost.size(); i++){
            int I = i;
            StorageReference Nama_Video = storageReference.child("Video_Kost").child(listVideoKost.get(i).getLastPathSegment() + ".mp4");
            Nama_Video.putFile(listVideoKost.get(i)).addOnProgressListener(snapshot ->
                    progressDialog.setMessage(getString(R.string.Mengunggah_Video) + " " + I + " " + getString(R.string.dari) + " " + (listVideoKost.size() - 1) + " (" +
                            ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount()) + "%)")
            ).addOnSuccessListener(taskSnapshot -> Nama_Video.getDownloadUrl().addOnSuccessListener(uri -> {
                databaseReference.child("Foto_Video_Kost").child("Video").push().setValue(uri.toString());

                if(I == (listVideoKost.size() - 1)){
                    progressDialog.dismiss();
                    CircularReveal.circularRevealCloseActivity(LLRootKelolaKost, this);
                }
            }));
        }
    }

    public void mengecekMengambilKelolaDataKostSudahAda(){
        CheckBox[] checkBoxFasilitas    = {CBFasilitasWifi, CBFasilitasCCTV, CBFasilitasJemuran, CBFasilitasLaundry, CBFasilitasParkirMotor, CBFasilitasParkirMobil,
                                            CBFasilitasSumberAirTanah, CBFasilitasSumberAirPam, CBFasilitasRuangBersama, CBFasilitasDapur, CBFasilitasTelevisi,
                                            CBFasilitasKulkas, CBFasilitasKeamanan, CBFasilitasDispenser, CBFasilitasKamarMandiDalam, CBFasilitasKamarMandiLuar,
                                            CBFasilitasToiletJongkok, CBFasilitasToiletDuduk, CBFasilitasKamarMandiBak, CBFasilitasKamarMandiShower,CBFasilitasKamarAC,
                                            CBFasilitasTermasukListrik, CBFasilitasLemari, CBFasilitasTempatTidur};
        String[] fasilitasBersama       = {"Wifi", "CCTV", "Jemuran", "Laundry", "Parkir Motor", "Parkir Mobil", "Sumber Air Tanah", "Sumber Air PAM", "Ruang Bersama",
                                            "Dapur atau Pantry", "Televisi", "Kulkas", "Keamanan", "Dispenser", "Kamar Mandi Dalam", "Kamar Mandi Luar", "Toilet Jongkok",
                                            "Toilet Duduk", "Kamar Mandi Bak", "Shower", "Kamar AC", "Termasuk Listrik", "Lemari", "Tempat Tidur"};

        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", ""));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    txtDataNamaKost.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue()));
                    txtDataDeskripsiKost.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Deskripsi_Kost").getValue()));
                    txtDataPeraturanKost.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Peraturan_Kost").getValue()));

                    for(int i = 0; i < listMayoritasPenghuni.size(); i++){
                        if(listMayoritasPenghuni.get(i).equals(String.valueOf(snapshot.child("Data_Umum_Kost").child("Mayoritas_Penghuni").getValue()))){
                            spinnerDataMayoritasPenghuni.setSelection(i);
                        }
                    }

                    txtKostPerhari.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Hari").getValue()));
                    txtKostPerbulan.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Bulan").getValue()));
                    txtKostPertahun.setText(String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Tahun").getValue()));

                    for(DataSnapshot dataSnapshot : snapshot.child("Data_Umum_Kost").child("Nama_Kamar").getChildren()){
                        listNamaKamarKost.add(String.valueOf(dataSnapshot.getValue()));
                    }

                    for(DataSnapshot dataSnapshot : snapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                        listFotoKost.add(Uri.parse(String.valueOf(dataSnapshot.getValue())));
                        listUpdateFotoKost.add(String.valueOf(dataSnapshot.getValue()));
                    }

                    for(DataSnapshot dataSnapshot : snapshot.child("Foto_Video_Kost").child("Video").getChildren()){
                        listVideoKost.add(Uri.parse(String.valueOf(dataSnapshot.getValue())));
                        listUpdateVideoKost.add(String.valueOf(dataSnapshot.getValue()));
                    }

                    txtDataNamaKecamatan.setText(String.valueOf(snapshot.child("Lokasi_Kost").child("Kecamatan").getValue()));
                    txtDataNamaDesa.setText(String.valueOf(snapshot.child("Lokasi_Kost").child("Desa").getValue()));
                    txtDataAlamatKost.setText(String.valueOf(snapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue()));

                    if(GM != null){
                        double latitude     = Double.parseDouble(String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").getValue()));
                        double longitude    = Double.parseDouble(String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").getValue()));
                        LatLng latLng       = new LatLng(latitude, longitude);
                        markerOptions       = new MarkerOptions().position(latLng).title(getString(R.string.lokasi_kost));

                        GM.clear();
                        GM.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                        GM.addMarker(markerOptions);
                    }

                    for(DataSnapshot dataSnapshot : snapshot.child("Fasilitas_Bersama").getChildren()){
                        for(int i = 0; i < fasilitasBersama.length; i++){
                            if(fasilitasBersama[i].equals(String.valueOf(dataSnapshot.getValue()))){
                                checkBoxFasilitas[i].setChecked(true);
                            }
                        }
                    }

                    for(DataSnapshot dataSnapshot : snapshot.child("Fasilitas_Sekitar").getChildren()){
                        listFasilitasSekitar.add(String.valueOf(dataSnapshot.getValue()));
                    }

                    for(DataSnapshot dataSnapshot : snapshot.child("Dekat_Dengan").getChildren()){
                        listDekatDengan.add(String.valueOf(dataSnapshot.getValue()));
                    }

                    GVNamaKamarKost.setVisibility(View.VISIBLE);
                    btnSimpanDataKost.setText(R.string.Update_Data_Kost);
                    GVNamaKamarKost.setAdapter(new AdapterFasilitas(ActivityKelolaKost.this, listNamaKamarKost));
                    GVDataFotoKost.setAdapter(new AdapterFotoVideo(ActivityKelolaKost.this, listFotoKost, "Foto"));
                    GVDataVideoKost.setAdapter(new AdapterFotoVideo(ActivityKelolaKost.this, listVideoKost, "Video"));
                    GVFasilitasSekitar.setAdapter(new AdapterFasilitas(ActivityKelolaKost.this, listFasilitasSekitar));
                    GVDekatDengan.setAdapter(new AdapterFasilitas(ActivityKelolaKost.this, listDekatDengan));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onBackPressed() {
        CircularReveal.circularRevealCloseActivity(LLRootKelolaKost, this);
    }
}