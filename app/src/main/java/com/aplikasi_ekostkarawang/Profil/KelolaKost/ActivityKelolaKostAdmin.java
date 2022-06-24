package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Lain.AdapterKost;
import com.aplikasi_ekostkarawang.Lain.ArrayKost;
import com.aplikasi_ekostkarawang.Profil.VerifikasiAkun.ActivityVerifikasiAdmin;
import com.aplikasi_ekostkarawang.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Objects;

import ru.nikartm.support.ImageBadgeView;

public class ActivityKelolaKostAdmin extends AppCompatActivity {
    Toolbar toolbarKelolaKostAdmin;
    ImageBadgeView IBVVerifikasiAdmin;
    ImageButton IBCariKostAdmin;
    EditText txtCariKostAdmin;
    RecyclerView RVKelolaKostAdmin;
    RelativeLayout RLKelolaKostAdminKosong;
    TextView txtKelolaKostAdminKosong;

    DatabaseReference databaseReference;
    SharedPreferences SP;

    AdapterKost adapterKost;
    ArrayList<ArrayKost> arrayKost = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_kost_admin);

        toolbarKelolaKostAdmin      = findViewById(R.id.toolbarKelolaKostAdmin);
        IBVVerifikasiAdmin          = findViewById(R.id.IBVVerifikasiAdmin);
        IBCariKostAdmin             = findViewById(R.id.IBCariKostAdmin);
        txtCariKostAdmin            = findViewById(R.id.txtCariKostAdmin);
        RVKelolaKostAdmin           = findViewById(R.id.RVKelolaKostAdmin);
        RLKelolaKostAdminKosong     = findViewById(R.id.RLKelolaKostAdminKosong);
        txtKelolaKostAdminKosong    = findViewById(R.id.txtKelolaKostAdminKosong);

        SP                          = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        adapterKost                 = new AdapterKost(this, arrayKost);

        RVKelolaKostAdmin.setHasFixedSize(true);
        RVKelolaKostAdmin.setAdapter(adapterKost);
        RVKelolaKostAdmin.setItemAnimator(new DefaultItemAnimator());
        RVKelolaKostAdmin.setLayoutManager(new LinearLayoutManager(this));
        RVKelolaKostAdmin.addItemDecoration(new DividerItemDecoration(RVKelolaKostAdmin.getContext(), LinearLayoutManager.VERTICAL));

        cekVerifikasiAkun();
        mengambilDataKost();
        setSupportActionBar(toolbarKelolaKostAdmin);
        PushDownAnim.setPushDownAnimTo(IBCariKostAdmin);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void cekVerifikasiAkun(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifikasi").child("Admin").child("Verifikasi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    IBVVerifikasiAdmin.setBadgeValue(Integer.parseInt(String.valueOf(snapshot.getChildrenCount())));
                }
                else{
                    IBVVerifikasiAdmin.setBadgeValue(0);
                }
                IBVVerifikasiAdmin.setOnClickListener(v -> startActivity(new Intent(ActivityKelolaKostAdmin.this, ActivityVerifikasiAdmin.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengambilDataKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayKost.clear();

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String ID_Kost      = String.valueOf(dataSnapshot.getKey());
                        String Nama_Kost    = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                        String Alamat_Kost  = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                        String Desa         = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Desa").getValue());
                        String Penghuni     = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Mayoritas_Penghuni").getValue());
                        String Kecamatan    = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Kecamatan").getValue());
                        String Latitude     = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").getValue());
                        String Longitude    = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").getValue());
                        LatLng latLng       = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
//                        double jarak        = Jarak(lokasiTerdekat, latLng);
//                        String Jarak        = String.format(Locale.getDefault(), "%.2f", 000) + getString(R.string.km);

                        ArrayList<String> Foto_Kost         = new ArrayList<>();
                        ArrayList<String> Video_Kost        = new ArrayList<>();

                        for(DataSnapshot DS : dataSnapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                            Foto_Kost.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Foto_Video_Kost").child("Video").getChildren()){
                            Video_Kost.add(String.valueOf(DS.getValue()));
                        }

                        arrayKost.add(new ArrayKost(ID_Kost, Nama_Kost, Alamat_Kost, Desa, Kecamatan, null, Penghuni, Foto_Kost, Video_Kost));
                        adapterKost.notifyDataSetChanged();
                    }
                }
                else{
                    RLKelolaKostAdminKosong.setVisibility(View.VISIBLE);
                    RVKelolaKostAdmin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}