package com.aplikasi_ekostkarawang.Beranda.Favorite;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aplikasi_ekostkarawang.Beranda.AdapterDataKost;
import com.aplikasi_ekostkarawang.Beranda.ArrayDataKost;
import com.aplikasi_ekostkarawang.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityFavorite extends AppCompatActivity {
    LinearLayout LLRootFavorite;
    TextView txtPetunjukFavorite;
    SwipeRefreshLayout SRLListFavorite;
    RecyclerView RVKostFavorite;
    RelativeLayout RLFavoriteKosong;

    DatabaseReference databaseReference;
    SharedPreferences SP;

    AdapterFavorite adapterFavoritePencari;
    AdapterDataKost adapterFavoritePemilik;

    ArrayList<ArrayFavorite> arrayFavorite  = new ArrayList<>();
    ArrayList<String> listFavoritePencari   = new ArrayList<>();
    ArrayList<ArrayDataKost> arrayDataKost  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        setContentView(R.layout.activity_favorite);

        LLRootFavorite          = findViewById(R.id.LLRootFavorite);
        txtPetunjukFavorite     = findViewById(R.id.txtPetunjukFavorite);
        SRLListFavorite         = findViewById(R.id.SRLListFavorite);
        RVKostFavorite          = findViewById(R.id.RVKostFavorite);
        RLFavoriteKosong        = findViewById(R.id.RLFavoriteKosong);

        SP                      = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        adapterFavoritePencari  = new AdapterFavorite(ActivityFavorite.this, arrayFavorite);
        adapterFavoritePemilik  = new AdapterDataKost(ActivityFavorite.this, arrayDataKost);

        arrayFavorite.clear();
        arrayDataKost.clear();
        RVKostFavorite.setHasFixedSize(true);
        RVKostFavorite.setItemAnimator(new DefaultItemAnimator());
        RVKostFavorite.setLayoutManager(new LinearLayoutManager(this));
        RVKostFavorite.addItemDecoration(new DividerItemDecoration(RVKostFavorite.getContext(), LinearLayoutManager.VERTICAL));
        RLFavoriteKosong.setVisibility(View.GONE);

        sharedTransition(savedInstanceState);
        refreshMengambilDataFavorite();
        mengambilDataFavorite();
    }

    private void sharedTransition(Bundle savedInstanceState){
        if(savedInstanceState == null){
//            getSupportActionBar().hide();
            LLRootFavorite.setVisibility(View.INVISIBLE);

            ViewTreeObserver VTO = LLRootFavorite.getViewTreeObserver();
            if(VTO.isAlive()){
                VTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        LLRootFavorite.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    private void circularRevealActivity(){
        int revealX     = LLRootFavorite.getRight() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        int revealY     = LLRootFavorite.getTop() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        float radius    = Math.max(LLRootFavorite.getWidth(), LLRootFavorite.getHeight());
        Animator anim   = ViewAnimationUtils.createCircularReveal(LLRootFavorite, revealX+30, revealY+125, 0, radius);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(getSupportActionBar() != null){
                    if(SP.getString("MasukSebagai", "Pemilik").equals("Pemilik")){
                        getSupportActionBar().setTitle(R.string.Akun_Memfavoritkan_Kost_Anda);
                        txtPetunjukFavorite.setVisibility(View.GONE);
                    }
                    else{
                        getSupportActionBar().setTitle(R.string.Daftar_Kost_Favorit_Anda);
                        txtPetunjukFavorite.setVisibility(View.VISIBLE);
                    }
//                    getSupportActionBar().show();
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        anim.setDuration(500);
        LLRootFavorite.setVisibility(View.VISIBLE);
        anim.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            circularRevealCloseActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void circularRevealCloseActivity(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int revealX     = LLRootFavorite.getWidth() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
            int revealY     = LLRootFavorite.getTop() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
            float radius    = Math.max(LLRootFavorite.getWidth(), LLRootFavorite.getHeight());
            Animator anim   = ViewAnimationUtils.createCircularReveal(LLRootFavorite, revealX+30, revealY+125, radius, 0);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
//                    Objects.requireNonNull(getSupportActionBar()).hide();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LLRootFavorite.setVisibility(View.INVISIBLE);
                    finishAfterTransition();
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
            anim.setDuration(500);
            anim.start();
        }
        else{
            super.onBackPressed();
        }
    }

    public void refreshMengambilDataFavorite(){
        SRLListFavorite.setColorSchemeColors(getColor(R.color.colorPrimary));
        SRLListFavorite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayFavorite.clear();
                arrayDataKost.clear();
                listFavoritePencari.clear();
                mengambilDataFavorite();
            }
        });
    }

    public void mengambilDataFavorite(){
        if(SP.getString("MasukSebagai", "Pemilik").equals("Pemilik")){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
            databaseReference.orderByChild("Pemilik").equalTo(SP.getString("EmailAkun", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ID_Kost = "";
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ID_Kost = String.valueOf(dataSnapshot.getKey());
                    }

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(ID_Kost);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                SRLListFavorite.setVisibility(View.VISIBLE);
                                RLFavoriteKosong.setVisibility(View.GONE);

                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pencari");
                                    databaseReference.orderByChild("Email").equalTo(String.valueOf(dataSnapshot.getValue()))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot DS : snapshot.getChildren()){
                                                        String Nama_Lengkap = String.valueOf(DS.child("Nama_Lengkap").getValue());
                                                        String Email        = String.valueOf(DS.child("Email").getValue());
                                                        String Foto_Profil  = String.valueOf(DS.child("Foto_Profil").getValue());

                                                        RVKostFavorite.setAdapter(adapterFavoritePencari);
                                                        arrayFavorite.add(new ArrayFavorite(Nama_Lengkap, Email, Foto_Profil));
                                                        adapterFavoritePencari.notifyDataSetChanged();
                                                        SRLListFavorite.setRefreshing(false);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) { }
                                            });
                                }
                            }
                            else{
                                SRLListFavorite.setVisibility(View.GONE);
                                RLFavoriteKosong.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
        else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for(DataSnapshot DS : snapshot.child(String.valueOf(dataSnapshot.getKey())).getChildren()){
                            if(String.valueOf(DS.getValue()).equals(SP.getString("EmailAkun", ""))){
                                listFavoritePencari.add(String.valueOf(dataSnapshot.getKey()));
                            }
                        }
                    }

                    if(listFavoritePencari.size() > 0){
                        for(int i = 0; i < listFavoritePencari.size(); i++){
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(listFavoritePencari.get(i));
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String Nama_Kost            = String.valueOf(snapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                                    String Deskripsi_Kost       = String.valueOf(snapshot.child("Data_Umum_Kost").child("Deskripsi_Kost").getValue());
                                    String Total_Kamar          = String.valueOf(snapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Total_Kamar").getValue());
                                    String Sisa_Kamar           = String.valueOf(snapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Sisa_Kamar").getValue());
                                    String Mayoritas_Penghuni   = String.valueOf(snapshot.child("Data_Umum_Kost").child("Mayoritas_Penghuni").getValue());
                                    String Harga_Kost_Hari      = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Hari").getValue());
                                    String Harga_Kost_Bulan     = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Bulan").getValue());
                                    String Harga_Kost_Tahun     = String.valueOf(snapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Tahun").getValue());
                                    String Peraturan_Kost       = String.valueOf(snapshot.child("Data_Umum_Kost").child("Peraturan_Kost").getValue());
                                    String Pemilik              = String.valueOf(snapshot.child("Pemilik").getValue());
                                    String Kecamatan            = String.valueOf(snapshot.child("Lokasi_Kost").child("Kecamatan").getValue());
                                    String Desa                 = String.valueOf(snapshot.child("Lokasi_Kost").child("Desa").getValue());
                                    String Alamat_Kost          = String.valueOf(snapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                                    String Jarak                = "null";

                                    ArrayList<String> Nama_Kamar        = new ArrayList<>();
                                    ArrayList<String> Fasilitas_Bersama = new ArrayList<>();
                                    ArrayList<String> Fasilitas_Sekitar = new ArrayList<>();
                                    ArrayList<String> Dekat_Dengan      = new ArrayList<>();
                                    ArrayList<String> Foto_Kost         = new ArrayList<>();
                                    ArrayList<String> Video_Kost        = new ArrayList<>();

                                    for(DataSnapshot DS : snapshot.child("Data_Umum_Kost").child("Nama_Kamar").getChildren()){
                                        Nama_Kamar.add(String.valueOf(DS.getValue()));
                                    }

                                    for(DataSnapshot DS : snapshot.child("Fasilitas_Bersama").getChildren()){
                                        Fasilitas_Bersama.add(String.valueOf(DS.getValue()));
                                    }

                                    for(DataSnapshot DS : snapshot.child("Fasilitas_Sekitar").getChildren()){
                                        Fasilitas_Sekitar.add(String.valueOf(DS.getValue()));
                                    }

                                    for(DataSnapshot DS : snapshot.child("Dekat_Dengan").getChildren()){
                                        Dekat_Dengan.add(String.valueOf(DS.getValue()));
                                    }

                                    for(DataSnapshot DS : snapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                                        Foto_Kost.add(String.valueOf(DS.getValue()));
                                    }

                                    for(DataSnapshot DS : snapshot.child("Foto_Video_Kost").child("Video").getChildren()){
                                        Video_Kost.add(String.valueOf(DS.getValue()));
                                    }

                                    RVKostFavorite.setAdapter(adapterFavoritePemilik);
                                    arrayDataKost.add(new ArrayDataKost(Nama_Kost, Deskripsi_Kost, Total_Kamar, Sisa_Kamar, Mayoritas_Penghuni, Harga_Kost_Hari,
                                            Harga_Kost_Bulan, Harga_Kost_Tahun, Peraturan_Kost, Pemilik, Kecamatan, Desa, Alamat_Kost, Jarak, Nama_Kamar, Fasilitas_Bersama,
                                            Fasilitas_Sekitar, Dekat_Dengan, Foto_Kost, Video_Kost));
                                    adapterFavoritePemilik.notifyDataSetChanged();
                                    SRLListFavorite.setRefreshing(false);
                                    menghapusFavorite();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    }
                    else{
                        SRLListFavorite.setVisibility(View.GONE);
                        RLFavoriteKosong.setVisibility(View.VISIBLE);
                        txtPetunjukFavorite.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    public void menghapusFavorite(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final ArrayDataKost hapusFavorite   = arrayDataKost.get(viewHolder.getBindingAdapterPosition());
                final int posisi                    = viewHolder.getBindingAdapterPosition();
                Snackbar snackbar                   = Snackbar.make(RVKostFavorite, R.string.Favorite_di_Hapus, 3000);

                final CountDownTimer countDownTimer = new CountDownTimer(snackbar.getDuration(), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
                        databaseReference.orderByChild("Pemilik").equalTo(hapusFavorite.getPemilik()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(String.valueOf(dataSnapshot.getKey()));
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot DS : snapshot.getChildren()){
                                                if(String.valueOf(DS.getValue()).equals(SP.getString("EmailAkun", ""))){
                                                    DS.getRef().removeValue();

                                                    if(arrayDataKost.size() == 0){
                                                        SRLListFavorite.setVisibility(View.GONE);
                                                        RLFavoriteKosong.setVisibility(View.VISIBLE);
                                                        txtPetunjukFavorite.setVisibility(View.GONE);
                                                    }
                                                }
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
                }.start();

                arrayDataKost.remove(posisi);
                adapterFavoritePemilik.notifyItemRemoved(posisi);

                snackbar.setActionTextColor(getColor(R.color.colorPrimary));
                snackbar.getView().setBackgroundColor(getColor(R.color.hitam_transparan));
                snackbar.setAction(R.string.URUNGKAN, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countDownTimer.cancel();
                        arrayDataKost.add(posisi, hapusFavorite);
                        adapterFavoritePemilik.notifyItemInserted(posisi);
                    }
                }).show();
            }
        }).attachToRecyclerView(RVKostFavorite);
    }

    @Override
    public void onBackPressed() {
        circularRevealCloseActivity();
    }
}