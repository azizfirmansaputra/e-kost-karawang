package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplikasi_ekostkarawang.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Objects;

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback {
    Button btnPilihLokasi;
    SupportMapFragment SMP;

    SharedPreferences SP;
    MarkerOptions MO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnPilihLokasi  = findViewById(R.id.btnPilihLokasi);
        SMP             = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentGoogleMap);

        SP              = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);

        SMP.getMapAsync(ActivityMap.this);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PushDownAnim.setPushDownAnimTo(btnPilihLokasi);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        LatLng latLng   = new LatLng(SP.getFloat("Latitude", 0), SP.getFloat("Longitude", 0));
        MO              = new MarkerOptions().position(latLng).title(getString(R.string.lokasi_kost));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(MO);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MO.position(latLng).title(getString(R.string.lokasi_kost));

                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(MO);
            }
        });

        pilihLokasi(MO);
    }

    public void pilihLokasi(final MarkerOptions MO){
        btnPilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SP.edit().putFloat("Latitude", (float)MO.getPosition().latitude).apply();
                SP.edit().putFloat("Longitude", (float)MO.getPosition().longitude).apply();

                setResult(RESULT_OK, new Intent(ActivityMap.this, ActivityKelolaKost.class));
                finish();
            }
        });
    }
}