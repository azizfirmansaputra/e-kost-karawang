package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplikasi_ekostkarawang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class ActivityKelolaKost1 extends AppCompatActivity {
    RelativeLayout RLTambahDataKost;
    ImageView imgBelumAdaKost;

    DatabaseReference databaseReference;
    SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_kost1);

        RLTambahDataKost    = findViewById(R.id.RLTambahDataKost);
        imgBelumAdaKost     = findViewById(R.id.imgBelumAdaKost);

        SP                  = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);

        cekKost();
        tambahDataKost();
        keluarKelolaKost();
//        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Kelola_Kost);
        PushDownAnim.setPushDownAnimTo(imgBelumAdaKost);
    }

    public void cekKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
        databaseReference.orderByChild("Pemilik").equalTo(SP.getString("EmailAkun", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    RLTambahDataKost.setVisibility(View.GONE);
                }
                else{
                    RLTambahDataKost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void tambahDataKost(){
        imgBelumAdaKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityKelolaKost1.this, ActivityKelolaKost.class));
            }
        });
    }

    public void keluarKelolaKost(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SP.getString("KeluarKelolaKost", "Tidak").equals("Ya")){
                    SP.edit().putString("KeluarKelolaKost", "Tidak").apply();
                    finish();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}