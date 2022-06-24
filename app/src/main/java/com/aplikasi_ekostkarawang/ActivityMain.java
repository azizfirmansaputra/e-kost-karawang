package com.aplikasi_ekostkarawang;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aplikasi_ekostkarawang.Beranda.FragmentBeranda;
import com.aplikasi_ekostkarawang.Pemesanan.FragmentPemesanan;
import com.aplikasi_ekostkarawang.Pesan.FragmentPesan;
import com.aplikasi_ekostkarawang.Profil.FragmentProfil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class ActivityMain extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView BNV;

    SharedPreferences SP;
    FirebaseUser FU;

    int Time = 2000;
    long BackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BNV = findViewById(R.id.BNV);
        SP  = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        FU  = FirebaseAuth.getInstance().getCurrentUser();

        BNV.setOnNavigationItemSelectedListener(this);

        cekMenu();
        cekKeluar();
        izinAksesLokasi();
    }

    public void cekMenu(){
        if(SP.getString("MenuBawah", "Beranda").equals("Profil")){
            loadFragment(new FragmentProfil());
            BNV.getMenu().findItem(R.id.profil).setChecked(true);
            SP.edit().putString("MenuBawah", "Beranda").apply();
            enableMenuItem(true,true, true, false);
        }
        else if(SP.getString("MenuBawah", "Beranda").equals("Pesan")){
            loadFragment(new FragmentPesan());
            BNV.getMenu().findItem(R.id.chat).setChecked(true);
            SP.edit().putString("MenuBawah", "Beranda").apply();
            enableMenuItem(true,true, false, true);
        }
        else if(SP.getString("MenuBawah", "Beranda").equals("Pemesanan")){
            loadFragment(new FragmentPemesanan());
            BNV.getMenu().findItem(R.id.pemesanan).setChecked(true);
            SP.edit().putString("MenuBawah", "Beranda").apply();
            enableMenuItem(true,false, true, true);
        }
        else{
            loadFragment(new FragmentBeranda());
            BNV.getMenu().findItem(R.id.beranda).setChecked(true);
            SP.edit().putString("MenuBawah", "Beranda").apply();
            enableMenuItem(false,true, true, true);
        }
    }

    public void enableMenuItem(boolean Beranda, boolean Pemesanan, boolean Pesan, boolean Profil){
        BNV.getMenu().findItem(R.id.beranda).setEnabled(Beranda);
        BNV.getMenu().findItem(R.id.pemesanan).setEnabled(Pemesanan);
        BNV.getMenu().findItem(R.id.chat).setEnabled(Pesan);
        BNV.getMenu().findItem(R.id.profil).setEnabled(Profil);
    }

    public void izinAksesLokasi(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch(menuItem.getItemId()){
            case R.id.beranda:
                fragment = new FragmentBeranda();
                enableMenuItem(false,true, true, true);

                break;
            case R.id.pemesanan:
                fragment = new FragmentPemesanan();
                enableMenuItem(true,false, true, true);

                break;
            case R.id.chat:
                fragment = new FragmentPesan();
                enableMenuItem(true,true, false, true);

                break;
            case R.id.profil:
                fragment = new FragmentProfil();
                enableMenuItem(true,true, true, false);

                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            return true;
        }
        return false;
    }

    public void cekKeluar(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SP.getString("KeluarAplikasi", "Tidak").equals("Ya")){
                    SP.edit().putString("KeluarAplikasi", "Tidak").apply();
                    finish();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public void statusPengguna(String Status){
        HashMap<String, Object> statusPengguna = new HashMap<>();
        statusPengguna.put("Waktu", System.currentTimeMillis());
        statusPengguna.put("Status", Status);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
        databaseReference.orderByChild("Email").equalTo(FU.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.child("Status_Pengguna").getRef().setValue(statusPengguna);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void updateToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(SP.getString("MasukSebagai", "").equals("Admin")){
                FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Admin").child("Token").setValue(task.getResult());
            }
            else{
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
                databaseReference.orderByChild("Email").equalTo(FU.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.child("Token").getRef().setValue(task.getResult());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FU != null){
            statusPengguna("Online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(FU != null){
            statusPengguna("Offline");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(FU != null){
            statusPengguna("Online");
            updateToken();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(FU != null){
            statusPengguna("Offline");
        }
    }

    @Override
    public void onBackPressed() {
        if(Time + BackPressed > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Toast.makeText(this, R.string.Tekan_Sekali_Lagi_untuk_Keluar, Toast.LENGTH_SHORT).show();
        }
        BackPressed = System.currentTimeMillis();
    }
}