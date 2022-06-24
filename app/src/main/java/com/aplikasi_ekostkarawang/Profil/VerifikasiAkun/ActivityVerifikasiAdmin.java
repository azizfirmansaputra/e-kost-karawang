package com.aplikasi_ekostkarawang.Profil.VerifikasiAkun;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Lain.ArrayAkun;
import com.aplikasi_ekostkarawang.Lain.CircularReveal;
import com.aplikasi_ekostkarawang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityVerifikasiAdmin extends AppCompatActivity {
    LinearLayout LLRootVerifikasiAkunAdmin;
    Toolbar toolbarVerifikasiAkunAdmin;
    RecyclerView RVVerifikasiAkunAdmin;
    View viewDataKosong;

    AdapterVerifikasiAdmin adapterVerifikasiAdmin;

    ArrayList<String> arrayNotifikasi   = new ArrayList<>();
    ArrayList<ArrayAkun> arrayAkun      = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        setContentView(R.layout.activity_verifikasi_admin);

        LLRootVerifikasiAkunAdmin   = findViewById(R.id.LLRootVerifikasiAkunAdmin);
        toolbarVerifikasiAkunAdmin  = findViewById(R.id.toolbarVerifikasiAkunAdmin);
        RVVerifikasiAkunAdmin       = findViewById(R.id.RVVerifikasiAkunAdmin);
        viewDataKosong              = findViewById(R.id.viewDataKosong);

        adapterVerifikasiAdmin      = new AdapterVerifikasiAdmin(this, arrayAkun);

        RVVerifikasiAkunAdmin.setHasFixedSize(true);
        RVVerifikasiAkunAdmin.setAdapter(adapterVerifikasiAdmin);
        RVVerifikasiAkunAdmin.setItemAnimator(new DefaultItemAnimator());
        RVVerifikasiAkunAdmin.setLayoutManager(new LinearLayoutManager(this));
        RVVerifikasiAkunAdmin.addItemDecoration(new DividerItemDecoration(RVVerifikasiAkunAdmin.getContext(), LinearLayoutManager.VERTICAL));

        cekNotifikasi();
        setSupportActionBar(toolbarVerifikasiAkunAdmin);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        CircularReveal.circularRevealOpenActivity(savedInstanceState, LLRootVerifikasiAkunAdmin, this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            CircularReveal.circularRevealCloseActivity(LLRootVerifikasiAkunAdmin, this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void cekNotifikasi(){
        FirebaseDatabase.getInstance().getReference().child("Notifikasi").child("Admin").child("Verifikasi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayNotifikasi.clear();

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        arrayNotifikasi.add(String.valueOf(dataSnapshot.getValue()));
                    }
                }
                mengambilDataVerifikasiAkun();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengambilDataVerifikasiAkun(){
        FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik").orderByChild("Verifikasi").equalTo("Menunggu")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayAkun.clear();

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String ID_Akun      = String.valueOf(dataSnapshot.getKey());
                        String Nama_Lengkap = String.valueOf(dataSnapshot.child("Nama_Lengkap").getValue());
                        String Email        = String.valueOf(dataSnapshot.child("Email").getValue());
                        String Foto_Profil  = String.valueOf(dataSnapshot.child("Foto_Profil").getValue());
                        String Token        = String.valueOf(dataSnapshot.child("Token").getValue());

                        arrayAkun.add(new ArrayAkun(ID_Akun, Nama_Lengkap, Email, Foto_Profil, Token, arrayNotifikasi));
                        adapterVerifikasiAdmin.notifyDataSetChanged();

                        RVVerifikasiAkunAdmin.setVisibility(View.VISIBLE);
                        viewDataKosong.setVisibility(View.GONE);
                    }
                }
                else{
                    RVVerifikasiAkunAdmin.setVisibility(View.GONE);
                    viewDataKosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onBackPressed() {
        CircularReveal.circularRevealCloseActivity(LLRootVerifikasiAkunAdmin, this);
    }
}