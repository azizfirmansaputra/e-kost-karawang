package com.aplikasi_ekostkarawang.Pesan;

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

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Token;
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

public class FragmentPesan extends Fragment {
    private RecyclerView RVListPesan;
    private RelativeLayout RLPesanKosong;
    private TextView txtPesanKosong;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private SharedPreferences SP;

    private AdapterPengguna adapterPengguna;
    private ArrayList<ArrayPengguna> arrayPengguna  = new ArrayList<>();
    private ArrayList<String> listPengguna          = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view       = inflater.inflate(R.layout.fragment_pesan, container, false);

        RVListPesan     = view.findViewById(R.id.RVListPesan);
        RLPesanKosong   = view.findViewById(R.id.RLPesanKosong);
        txtPesanKosong  = view.findViewById(R.id.txtPesanKosong);

        FirebaseAuth FA     = FirebaseAuth.getInstance();
        firebaseUser        = FA.getCurrentUser();
        SP                  = requireContext().getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);

        RVListPesan.setHasFixedSize(true);
        RVListPesan.setAdapter(adapterPengguna);
        RVListPesan.setItemAnimator(new DefaultItemAnimator());
        RVListPesan.setLayoutManager(new LinearLayoutManager(getContext()));

        cekMasuk();
//        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token){
        databaseReference   = FirebaseDatabase.getInstance().getReference().child("Token");
        Token token1        = new Token(token);

        databaseReference.child(firebaseUser.getUid()).setValue(token);
    }

    private void cekMasuk(){
        if(firebaseUser == null){
            RVListPesan.setVisibility(View.GONE);
            RLPesanKosong.setVisibility(View.VISIBLE);
            txtPesanKosong.setText(R.string.Anda_Belum_Login);
            PushDownAnim.setPushDownAnimTo(txtPesanKosong);

            txtPesanKosong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SP.edit().putString("MenuBawah", "Profil").apply();
                    startActivity(new Intent(getContext(), ActivityMain.class));
                    requireActivity().finishAfterTransition();
                }
            });
        }
        else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Pesan");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listPengguna.clear();

                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ArrayPesan Pesan = dataSnapshot.getValue(ArrayPesan.class);

                            if(Pesan.getPengirim().equals(firebaseUser.getEmail())){
                                listPengguna.add(Pesan.getPenerima());
                            }

                            if(Pesan.getPenerima().equals(firebaseUser.getEmail())){
                                listPengguna.add(Pesan.getPengirim());
                            }
                        }

                        bacaPesan();
                    }
                    else{
                        RVListPesan.setVisibility(View.GONE);
                        RLPesanKosong.setVisibility(View.VISIBLE);
                        txtPesanKosong.setText(R.string.pesan_kosong);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    private void bacaPesan(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayPengguna.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ArrayPengguna Pengguna = dataSnapshot.getValue(ArrayPengguna.class);

                    for(String Email : listPengguna){
                        if(Pengguna.getEmail().equals(Email)){
                            if(arrayPengguna.size() != 0){
                                for(ArrayPengguna AP : arrayPengguna){
                                    if(!Pengguna.getEmail().equals(AP.getEmail())){
                                        arrayPengguna.add(Pengguna);
                                    }
                                }
                            }
                            else{
                                arrayPengguna.add(Pengguna);
                            }
                        }
                    }
                }

                adapterPengguna = new AdapterPengguna(getContext(), arrayPengguna, true);
                RVListPesan.setAdapter(adapterPengguna);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}