package com.aplikasi_ekostkarawang.Pemesanan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

public class FragmentPemesanan extends Fragment {
    private SwipeRefreshLayout SRLLisPemesanan;
    private RecyclerView RVPemesananKost;
    private RelativeLayout RLPesananKosong;
    private TextView txtPesananKosong;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private SharedPreferences SP;

    private AdapterPemesanan adapterPemesanan;
    private ArrayList<ArrayPemesanan> arrayPemesanan = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view           = inflater.inflate(R.layout.fragment_pemesanan, container, false);

        SRLLisPemesanan     = view.findViewById(R.id.SRLLisPemesanan);
        RVPemesananKost     = view.findViewById(R.id.RVPemesananKost);
        RLPesananKosong     = view.findViewById(R.id.RLPesananKosong);
        txtPesananKosong    = view.findViewById(R.id.txtPesananKosong);

        FirebaseAuth FA     = FirebaseAuth.getInstance();
        firebaseUser        = FA.getCurrentUser();
        SP                  = requireContext().getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);
        adapterPemesanan    = new AdapterPemesanan(getContext(), arrayPemesanan);

        arrayPemesanan.clear();
        RVPemesananKost.setHasFixedSize(true);
        RVPemesananKost.setAdapter(adapterPemesanan);
        RVPemesananKost.setItemAnimator(new DefaultItemAnimator());
        RVPemesananKost.setLayoutManager(new LinearLayoutManager(getContext()));
        RVPemesananKost.addItemDecoration(new DividerItemDecoration(RVPemesananKost.getContext(), LinearLayoutManager.VERTICAL));

        cekMasuk();
        segarkanPemesanan();

        return view;
    }

    private void segarkanPemesanan(){
        SRLLisPemesanan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayPemesanan.clear();
                cekMasuk();
            }
        });
    }

    private void cekMasuk(){
        if(firebaseUser == null){
            SRLLisPemesanan.setVisibility(View.GONE);
            RLPesananKosong.setVisibility(View.VISIBLE);
            txtPesananKosong.setText(R.string.Anda_Belum_Login);
            PushDownAnim.setPushDownAnimTo(txtPesananKosong);

            txtPesananKosong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SP.edit().putString("MenuBawah", "Profil").apply();
                    startActivity(new Intent(getContext(), ActivityMain.class));
                    requireActivity().finishAfterTransition();
                }
            });
        }
        else{
            if(SP.getString("MasukSebagai", "").equals("Pencari")){
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                databaseReference.orderByChild("Email_Pemesan").equalTo(SP.getString("EmailAkun", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                final String Nama_Pemesan             = String.valueOf(dataSnapshot.child("Nama_Pemesan").getValue());
                                final String Email_Pemesan            = String.valueOf(dataSnapshot.child("Email_Pemesan").getValue());
                                final String Nomor_Pemesanan          = String.valueOf(dataSnapshot.child("Nomor_Pemesanan").getValue());
                                final String Status_Pemesanan         = String.valueOf(dataSnapshot.child("Status_Pemesanan").getValue());
                                final String Tanggal_Masuk            = String.valueOf(dataSnapshot.child("Tanggal_Masuk").getValue());
                                final String Nomor_Pembayaran         = String.valueOf(dataSnapshot.child("Nomor_Pembayaran").getValue());
                                final String Metode_Pembayaran        = String.valueOf(dataSnapshot.child("Metode_Pembayaran").getValue());
                                final String ID_Kost                  = String.valueOf(dataSnapshot.child("DataKost").child("ID_Kost").getValue());
                                final String Nama_Kamar               = String.valueOf(dataSnapshot.child("DataKost").child("Nama_Kamar").getValue());
                                final String Sewa_Per                 = String.valueOf(dataSnapshot.child("DataKost").child("Sewa_Per").getValue());
                                final String Harga_Kost               = String.valueOf(dataSnapshot.child("DataKost").child("Harga_Kost").getValue());
                                final ArrayList<String> arrayFotoKost = new ArrayList<>();

                                databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(ID_Kost);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String Nama_Kost    = String.valueOf(snapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                                        String Alamat       = String.valueOf(snapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                                        String Desa         = String.valueOf(snapshot.child("Lokasi_Kost").child("Desa").getValue());
                                        String Kecamatan    = String.valueOf(snapshot.child("Lokasi_Kost").child("Kecamatan").getValue());
                                        String Alamat_Kost  = Alamat + ", " + Desa + ", " + Kecamatan;

                                        for(DataSnapshot DS : snapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                                            arrayFotoKost.add(String.valueOf(DS.getValue()));
                                        }

                                        arrayPemesanan.add(new ArrayPemesanan(Nama_Pemesan, Email_Pemesan, Nomor_Pemesanan, Status_Pemesanan, Tanggal_Masuk, Nomor_Pembayaran,
                                                Metode_Pembayaran, ID_Kost, Nama_Kost, Nama_Kamar, Sewa_Per, Harga_Kost, Alamat_Kost, arrayFotoKost));
                                        adapterPemesanan.notifyDataSetChanged();
                                        SRLLisPemesanan.setRefreshing(false);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            }
                            SRLLisPemesanan.setVisibility(View.VISIBLE);
                            RLPesananKosong.setVisibility(View.GONE);
                        }
                        else{
                            SRLLisPemesanan.setVisibility(View.GONE);
                            RLPesananKosong.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            else if(SP.getString("MasukSebagai", "").equals("Pemilik")){
                databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
                databaseReference.orderByChild("Pemilik").equalTo(SP.getString("EmailAkun", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(final DataSnapshot dataSnapshot : snapshot.getChildren()){
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot DS : snapshot.getChildren()){
                                            if(String.valueOf(DS.child("DataKost").child("ID_Kost").getValue()).equals(dataSnapshot.getKey())){
                                                final String Nama_Pemesan             = String.valueOf(DS.child("Nama_Pemesan").getValue());
                                                final String Email_Pemesan            = String.valueOf(DS.child("Email_Pemesan").getValue());
                                                final String Nomor_Pemesanan          = String.valueOf(DS.child("Nomor_Pemesanan").getValue());
                                                final String Status_Pemesanan         = String.valueOf(DS.child("Status_Pemesanan").getValue());
                                                final String Tanggal_Masuk            = String.valueOf(DS.child("Tanggal_Masuk").getValue());
                                                final String Nomor_Pembayaran         = String.valueOf(DS.child("Nomor_Pembayaran").getValue());
                                                final String Metode_Pembayaran        = String.valueOf(DS.child("Metode_Pembayaran").getValue());
                                                final String ID_Kost                  = String.valueOf(DS.child("DataKost").child("ID_Kost").getValue());
                                                final String Nama_Kamar               = String.valueOf(DS.child("DataKost").child("Nama_Kamar").getValue());
                                                final String Sewa_Per                 = String.valueOf(DS.child("DataKost").child("Sewa_Per").getValue());
                                                final String Harga_Kost               = String.valueOf(DS.child("DataKost").child("Harga_Kost").getValue());
                                                final ArrayList<String> arrayFotoKost = new ArrayList<>();

                                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pencari");
                                                databaseReference.orderByChild("Email").equalTo(Email_Pemesan).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot DS : snapshot.getChildren()){
                                                            arrayFotoKost.add(String.valueOf(DS.child("Foto_Profil").getValue()));
                                                        }

                                                        arrayPemesanan.add(new ArrayPemesanan(Nama_Pemesan, Email_Pemesan, Nomor_Pemesanan, Status_Pemesanan, Tanggal_Masuk,
                                                                Nomor_Pembayaran, Metode_Pembayaran, ID_Kost, Nama_Pemesan, Nama_Kamar, Sewa_Per, Harga_Kost, Email_Pemesan,
                                                                arrayFotoKost));
                                                        adapterPemesanan.notifyDataSetChanged();
                                                        SRLLisPemesanan.setRefreshing(false);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) { }
                                                });
                                            }
                                        }
                                    }
                                    else{
                                        SRLLisPemesanan.setVisibility(View.GONE);
                                        RLPesananKosong.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            else{
                Toast.makeText(getContext(), "Anda Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterPemesanan.notifyDataSetChanged();
    }
}