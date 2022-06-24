package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.Beranda.AdapterDataKost;
import com.aplikasi_ekostkarawang.Beranda.ArrayDataKost;
import com.aplikasi_ekostkarawang.Lain.CustomGridView;
import com.aplikasi_ekostkarawang.Pemesanan.ActivityPemesanan;
import com.aplikasi_ekostkarawang.Pesan.ActivityPesan;
import com.aplikasi_ekostkarawang.Profil.KelolaKost.ActivityKelolaKost;
import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ActivitySelengkapnya extends AppCompatActivity {
    Toolbar toolbarSelengkapnya;
    ImageButton IBActionSelengkapnya;
    ViewPager VPMediaKost;
    LinearLayout LLDotIndicator, LLLokasiKost, LLFasilitasSekitar, LLDekatDengan, LLPesanKamarKost;
    Button btnChatKost, btnBagikanKost;
    TextView txtNamaKost, txtAlamatKost, txtJarakKost, txtIconMayoritasPenghuni, txtMayoritasPenghuni, txtRatingBarKost, txtSisaKamarKost, txtNamaPemilikKost, txtTerakhirAktif,
            txtDeskripsiKost, txtUlasanKostKosong, txtHargaKost, txtHargaPer;
    RatingBar ratingBarKost;
    ImageView imgFotoPemilikKost;
    CustomGridView GVFasilitasBersama;
    RecyclerView RVUlasanKost;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    MenuItem menuItemFavorite;
    SharedPreferences SP;

    ViewPagerMedia viewPagerMedia;
    AdapterFasilitasBersama adapterFasilitasBersama;
    AdapterUlasan adapterUlasan;

    int Posisi;
    String IDKost;
    boolean Favorite;
    TextView[] txtDots;
    String Latitude, Longitude, SewaKost, NamaKamar, FotoPemilik, NamaPemilik;
    ArrayList<ArrayDataKost> arrayDataKost  = new ArrayList<>();
    ArrayList<String> arrayListMediaKost    = new ArrayList<>();
    ArrayList<ArrayUlasan> arrayUlasan      = new ArrayList<>();

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_selengkapnya);

        IBActionSelengkapnya        = findViewById(R.id.IBActionSelengkapnya);
        toolbarSelengkapnya         = findViewById(R.id.toolbarSelengkapnya);
        VPMediaKost                 = findViewById(R.id.VPMediaKost);
        LLDotIndicator              = findViewById(R.id.LLDotIndicator);
        btnChatKost                 = findViewById(R.id.btnChatKost);
        btnBagikanKost              = findViewById(R.id.btnBagikanKost);
        txtNamaKost                 = findViewById(R.id.txtNamaKost);
        txtAlamatKost               = findViewById(R.id.txtAlamatKost);
        LLLokasiKost                = findViewById(R.id.LLLokasiKost);
        txtJarakKost                = findViewById(R.id.txtJarakKost);
        txtIconMayoritasPenghuni    = findViewById(R.id.txtIconMayoritasPenghuni);
        txtMayoritasPenghuni        = findViewById(R.id.txtMayoritasPenghuni);
        ratingBarKost               = findViewById(R.id.ratingBarKost);
        txtRatingBarKost            = findViewById(R.id.txtRatingBarKost);
        txtSisaKamarKost            = findViewById(R.id.txtSisaKamarKost);
        imgFotoPemilikKost          = findViewById(R.id.imgFotoPemilikKost);
        txtNamaPemilikKost          = findViewById(R.id.txtNamaPemilikKost);
        txtTerakhirAktif            = findViewById(R.id.txtTerakhirAktif);
        GVFasilitasBersama          = findViewById(R.id.GVFasilitasBersama);
        txtDeskripsiKost            = findViewById(R.id.txtDeskripsiKost);
        LLFasilitasSekitar          = findViewById(R.id.LLFasilitasSekitar);
        LLDekatDengan               = findViewById(R.id.LLDekatDengan);
        txtUlasanKostKosong         = findViewById(R.id.txtUlasanKostKosong);
        RVUlasanKost                = findViewById(R.id.RVUlasanKost);
        txtHargaKost                = findViewById(R.id.txtHargaKost);
        txtHargaPer                 = findViewById(R.id.txtHargaPer);
        LLPesanKamarKost            = findViewById(R.id.LLPesanKamarKost);

        arrayDataKost               = AdapterDataKost.arrayDataKost;
        Posisi                      = Objects.requireNonNull(getIntent().getExtras()).getInt("Posisi");
        SP                          = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        viewPagerMedia              = new ViewPagerMedia(this, arrayListMediaKost);
        adapterFasilitasBersama     = new AdapterFasilitasBersama(this, arrayDataKost.get(Posisi).getFasilitas_Bersama());
        adapterUlasan               = new AdapterUlasan(this, arrayUlasan);

        FirebaseAuth FA             = FirebaseAuth.getInstance();
        firebaseUser                = FA.getCurrentUser();

        setSupportActionBar(toolbarSelengkapnya);

        if(getIntent().getExtras().getString("IDKost").equals(SP.getString("IDAkun", ""))){
            FirebaseDatabase.getInstance().getReference().child("DataKost").child(SP.getString("IDAkun", ""))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                IBActionSelengkapnya.setImageDrawable(getDrawable(R.drawable.icon_edit));
                                IBActionSelengkapnya.setOnClickListener(v -> startActivity(new Intent(ActivitySelengkapnya.this, ActivityKelolaKost.class)));
                            }
                            else{
                                IBActionSelengkapnya.setImageDrawable(getDrawable(R.drawable.icon_plus));
                                IBActionSelengkapnya.setOnClickListener(v -> startActivity(new Intent(ActivitySelengkapnya.this, ActivityKelolaKost.class)));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
        }
        else{
            IBActionSelengkapnya.setImageDrawable(getDrawable(R.drawable.icon_favorite));
        }

//        arrayListMediaKost.addAll(arrayDataKost.get(Posisi).getFoto_Kost());
//        arrayListMediaKost.addAll(arrayDataKost.get(Posisi).getVideo_Kost());
//        Collections.shuffle(arrayListMediaKost);
//        VPMediaKost.setAdapter(viewPagerMedia);
//
//        SP.edit().putString("FullScreen", "Tidak").apply();
//        txtAlamatKost.setText(arrayDataKost.get(Posisi).getAlamat() + "\n" + arrayDataKost.get(Posisi).getDesa() + ", " + arrayDataKost.get(Posisi).getKecamatan());
//        txtSisaKamarKost.setText(getString(R.string.Sisa) + arrayDataKost.get(Posisi).getSisa_Kamar() + getString(R.string.Kamar));
//        txtNamaKost.setText(arrayDataKost.get(Posisi).getNama_Kost());
//        txtJarakKost.setText(arrayDataKost.get(Posisi).getJarak());
//        LLLokasiKost.setEnabled(false);
//
//        GVFasilitasBersama.setEnabled(false);
//        GVFasilitasBersama.setExpanded(true);
//        GVFasilitasBersama.setAdapter(adapterFasilitasBersama);
//        adapterFasilitasBersama.notifyDataSetChanged();
//        txtDeskripsiKost.setText(arrayDataKost.get(Posisi).getDeskripsi_Kost());
//        fasilitasSekitarDekatDengan(arrayDataKost.get(Posisi).getDekat_Dengan(), getString(R.string.dekat_dengan), LLDekatDengan);
//        fasilitasSekitarDekatDengan(arrayDataKost.get(Posisi).getFasilitas_Sekitar(), getString(R.string.fasilitas_sekitar), LLFasilitasSekitar);
//
//        arrayUlasan.clear();
//        RVUlasanKost.setHasFixedSize(true);
//        RVUlasanKost.setAdapter(adapterUlasan);
//        RVUlasanKost.setItemAnimator(new DefaultItemAnimator());
//        RVUlasanKost.setLayoutManager(new LinearLayoutManager(this));
//        RVUlasanKost.addItemDecoration(new DividerItemDecoration(RVUlasanKost.getContext(), LinearLayoutManager.VERTICAL));

//        mengambilIDKost();
//        mengaturMediaKost();
//        pesanBagikanKost();
//        mayoritasPenghuniKost();
//        informasiPemilikKost();
//        menuPesanKamarKost();
//        Objects.requireNonNull(getSupportActionBar()).setTitle(arrayDataKost.get(Posisi).getNama_Kost());
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.hitam_transparan)));
//        PushDownAnim.setPushDownAnimTo(btnChatKost, btnBagikanKost, LLPesanKamarKost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        menuItemFavorite = menu.findItem(R.id.menu_favorite);

        if(firebaseUser != null){
            if(SP.getString("MasukSebagai", "").equals("Pencari")){
                menuItemFavorite.setVisible(true);
                menuItemFavorite.setEnabled(false);
            }
            else{
                menuItemFavorite.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_favorite){
            if(firebaseUser == null){
                menuItemFavorite.setEnabled(true);
                belumLogin();
            }
            else{
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(IDKost);
                if(Favorite){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(String.valueOf(dataSnapshot.getValue()).equals(SP.getString("EmailAkun", ""))){
                                    dataSnapshot.getRef().removeValue();
                                    menuItemFavorite.setIcon(R.drawable.icon_favorite);
                                    Favorite = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                else{
                    databaseReference.push().setValue(SP.getString("EmailAkun", "")).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            menuItemFavorite.setIcon(R.drawable.icon_favorite_isi);
                            Favorite = true;
                        }
                    });
                }
            }
        }
        return true;
    }

    public void belumLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setIcon(R.drawable.icon_profil)
                .setTitle(R.string.Belum_Login)
                .setMessage(R.string.Anda_Belum_Login__Login_Sekarang_)
                .setNegativeButton(R.string.TIDAK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.YA, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SP.edit().putString("MenuBawah", "Profil").apply();
                        startActivity(new Intent(ActivitySelengkapnya.this, ActivityMain.class));
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setCancelable(true);
        builder.show();
    }

    public void mengambilIDKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
        databaseReference.orderByChild("Pemilik").equalTo(arrayDataKost.get(Posisi).getPemilik()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    IDKost = String.valueOf(dataSnapshot.getKey());
                }

                if(firebaseUser != null){
                    mengambilDataFavorite();
                }
                mengambilJarakKost();
                ratingBarKost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengambilDataFavorite(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(IDKost);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(String.valueOf(dataSnapshot.getValue()).equals(SP.getString("EmailAkun", ""))){
                        Favorite = true;
                    }
                }

                if(Favorite){
                    menuItemFavorite.setIcon(R.drawable.icon_favorite_isi);
                }
                else{
                    menuItemFavorite.setIcon(R.drawable.icon_favorite);
                }
                menuItemFavorite.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengaturMediaKost(){
        setDot(0);
        VPMediaKost.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                setDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    public void setDot(int posisi){
        txtDots = new TextView[arrayListMediaKost.size()];
        LLDotIndicator.removeAllViews();

        for(int i = 0; i < txtDots.length; i++){
            txtDots[i] = new TextView(this);
            txtDots[i].setText(Html.fromHtml("&#9673;"));
            txtDots[i].setTextColor(getColor(R.color.abuabu));
            txtDots[i].setTextSize(10);
            LLDotIndicator.addView(txtDots[i]);
        }
        txtDots[posisi].setTextColor(getColor(R.color.colorPrimary));
    }

    public void pesanBagikanKost(){
        btnChatKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser == null){
                    belumLogin();
                }
                else{
                    startActivity(new Intent(ActivitySelengkapnya.this, ActivityPesan.class)
                            .putExtra("Posisi", Posisi)
                            .putExtra("FotoPemilik", FotoPemilik)
                            .putExtra("NamaPemilik", NamaPemilik));
                }
            }
        });

        btnBagikanKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Bagikan  = new Intent(Intent.ACTION_SEND);
                String URL      = getString(R.string.www_e_kostkarawang_com) + "/" + arrayDataKost.get(Posisi).getPemilik();

                Bagikan.setType("text/plain");
                Bagikan.putExtra(Intent.EXTRA_TEXT, URL);
                startActivity(Intent.createChooser(Bagikan, getString(R.string.Bagikan_dengan)));
            }
        });
    }

    public void mengambilJarakKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(IDKost);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Latitude    = String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").getValue());
                Longitude   = String.valueOf(snapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").getValue());

                if(arrayDataKost.get(Posisi).getJarak().equals("null")){
                    LatLng lokasiTerdekat   = new LatLng(SP.getFloat("Latitude", 0), SP.getFloat("Longitude", 0));
                    LatLng cariLokasi       = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                    double jarak            = Jarak(lokasiTerdekat, cariLokasi);
                    String Jarak            = String.format(Locale.getDefault(), "%.2f", jarak) + getString(R.string.km);

                    txtJarakKost.setText(Jarak);
                }
                LLLokasiKost.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        LLLokasiKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri lokasiKostUri   = Uri.parse("google.navigation:q=" + Double.parseDouble(Latitude) +"," + Double.parseDouble(Longitude));
                Intent LokasiKost   = new Intent(Intent.ACTION_VIEW, lokasiKostUri);

                LokasiKost.setPackage("com.google.android.apps.maps");
                startActivity(LokasiKost);
            }
        });
    }

    private double Jarak(LatLng lokasiTerdekat, LatLng cariLokasi){
        double longDiff = lokasiTerdekat.longitude - cariLokasi.longitude;
        double Jarak    = Math.sin(Math.toRadians(lokasiTerdekat.latitude)) * Math.sin(Math.toRadians(cariLokasi.latitude)) +
                            Math.cos(Math.toRadians(lokasiTerdekat.latitude)) * Math.cos(Math.toRadians(cariLokasi.latitude)) *
                                Math.cos(Math.toRadians(longDiff));
        Jarak           = Math.acos(Jarak);
        Jarak           = Math.toDegrees(Jarak);
        Jarak           = Jarak * 60 * 1.1515;
        Jarak           = Jarak * 1.609344;

        return Jarak;
    }

    public void ratingBarKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ulasan").child(IDKost);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    float total = 0;
                    int hitung  = 0;

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        float rating    = Float.parseFloat(String.valueOf(dataSnapshot.child("Rating").getValue()));
                        total           = total + rating;
                        hitung          = hitung + 1;

                        String Isi_Ulasan   = String.valueOf(dataSnapshot.child("Isi_Ulasan").getValue());
                        String Nama_Akun    = String.valueOf(dataSnapshot.child("Nama_Akun").getValue());
                        String Rating       = String.valueOf(dataSnapshot.child("Rating").getValue());
                        String Tanggal      = String.valueOf(dataSnapshot.child("Tanggal").getValue());

                        arrayUlasan.add(new ArrayUlasan(Isi_Ulasan, Nama_Akun, Rating, Tanggal));
                        adapterUlasan.notifyDataSetChanged();
                    }

                    ratingBarKost.setRating(total/hitung);
                    txtRatingBarKost.setText(String.valueOf(total/hitung));
                    RVUlasanKost.setVisibility(View.VISIBLE);
                    txtUlasanKostKosong.setVisibility(View.GONE);
                    SP.edit().putString("ID_KOST", IDKost).apply();
                }
                else{
                    ratingBarKost.setRating(0);
                    txtRatingBarKost.setText(getString(R.string._0_0));
                    RVUlasanKost.setVisibility(View.GONE);
                    txtUlasanKostKosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mayoritasPenghuniKost(){
        if(arrayDataKost.get(Posisi).getMayoritas_Penghuni().equals("Pria")){
            txtIconMayoritasPenghuni.setBackground(getDrawable(R.drawable.icon_pria));
            txtMayoritasPenghuni.setText(getString(R.string.pria));
        }
        else if(arrayDataKost.get(Posisi).getMayoritas_Penghuni().equals("Wanita")){
            txtIconMayoritasPenghuni.setBackground(getDrawable(R.drawable.icon_wanita));
            txtMayoritasPenghuni.setText(getString(R.string.wanita));
        }
        else{
            txtIconMayoritasPenghuni.setBackground(null);
            txtMayoritasPenghuni.setText(getString(R.string.umum));
        }
    }

    public void informasiPemilikKost(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik");
        databaseReference.orderByChild("Email").equalTo(arrayDataKost.get(Posisi).getPemilik()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FotoPemilik = String.valueOf(dataSnapshot.child("Foto_Profil").getValue());
                    NamaPemilik = String.valueOf(dataSnapshot.child("Nama_Lengkap").getValue());

                    if(!String.valueOf(dataSnapshot.child("Foto_Profil").getValue()).equals("")){
                        Glide.with(ActivitySelengkapnya.this).asBitmap().load(String.valueOf(dataSnapshot.child("Foto_Profil").getValue())).into(imgFotoPemilikKost);
                    }
                    txtNamaPemilikKost.setText(String.valueOf(dataSnapshot.child("Nama_Lengkap").getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @SuppressLint("SetTextI18n")
    public void fasilitasSekitarDekatDengan(ArrayList<String> arrayList, String Judul, LinearLayout linearLayout){
        TextView[] txt      = new TextView[arrayList.size()];
        TextView txtJudul   = new TextView(this);

        linearLayout.removeAllViews();
        if(arrayList.size() != 0){
            txtJudul.setText(Judul);
            txtJudul.setTextSize(15);
            txtJudul.setTextColor(getColor(R.color.abuabu));
            txtJudul.setTypeface(txtJudul.getTypeface(), Typeface.BOLD);
            linearLayout.addView(txtJudul);
        }

        for(int i = 0; i < txt.length; i++){
            txt[i] = new TextView(this);
            txt[i].setText(Html.fromHtml("&#10004; ") + arrayList.get(i));
            txt[i].setTextColor(getColor(R.color.abuabu));
            txt[i].setTextSize(15);
            linearLayout.addView(txt[i]);
        }
    }

    public void menuPesanKamarKost(){
        if(!arrayDataKost.get(Posisi).getHarga_Kost_Bulan().equals("0")){
            txtHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Bulan());
            txtHargaPer.setText(getString(R.string._bulan));
        }
        else if(!arrayDataKost.get(Posisi).getHarga_Kost_Tahun().equals("0")){
            txtHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Tahun());
            txtHargaPer.setText(getString(R.string._tahun));
        }
        else{
            txtHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Hari());
            txtHargaPer.setText(getString(R.string._hari));
        }

        LLPesanKamarKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog BSD = new BottomSheetDialog(ActivitySelengkapnya.this);
                BSD.setContentView(R.layout.bottom_menu_pesan);
                BSD.setCanceledOnTouchOutside(true);
                BSD.show();

                ImageView imgKonfirmasiKost                 = BSD.findViewById(R.id.imgKonfirmasiKost);
                TextView txtKonfirmasiNamaKost              = BSD.findViewById(R.id.txtKonfirmasiNamaKost);
                final TextView txtKonfirmasiHargaKost       = BSD.findViewById(R.id.txtKonfirmasiHargaKost);
                TextView txtKonfirmasiSisaKamar             = BSD.findViewById(R.id.txtKonfirmasiSisaKamar);
                LinearLayout LLKonfirmasiSewaKostPer        = BSD.findViewById(R.id.LLKonfirmasiSewaKostPer);
                final RadioGroup RGKonfirmasiSewaKost       = BSD.findViewById(R.id.RGKonfirmasiSewaKost);
                final RadioButton RBKonfirmasiSewaKostHari  = BSD.findViewById(R.id.RBKonfirmasiSewaKostHari);
                final RadioButton RBKonfirmasiSewaKostBulan = BSD.findViewById(R.id.RBKonfirmasiSewaKostBulan);
                final RadioButton RBKonfirmasiSewaKostTahun = BSD.findViewById(R.id.RBKonfirmasiSewaKostTahun);
                final RadioGroup RGKonfirmasiNamaKamar      = BSD.findViewById(R.id.RGKonfirmasiNamaKamar);
                final Button btnKonfirmasiPesanKamar        = BSD.findViewById(R.id.btnKonfirmasiPesanKamar);

                Glide.with(ActivitySelengkapnya.this).asBitmap().load(arrayDataKost.get(Posisi).getFoto_Kost().get(0)).into(Objects.requireNonNull(imgKonfirmasiKost));
                Objects.requireNonNull(txtKonfirmasiNamaKost).setText(arrayDataKost.get(Posisi).getNama_Kost());
                Objects.requireNonNull(txtKonfirmasiHargaKost).setText(txtHargaKost.getText().toString());
                Objects.requireNonNull(txtKonfirmasiSisaKamar).setText(txtSisaKamarKost.getText().toString());

                if((arrayDataKost.get(Posisi).getHarga_Kost_Hari().equals("0") && arrayDataKost.get(Posisi).getHarga_Kost_Tahun().equals("0")) ||
                        (arrayDataKost.get(Posisi).getHarga_Kost_Hari().equals("0") && arrayDataKost.get(Posisi).getHarga_Kost_Bulan().equals("0")) ||
                        (arrayDataKost.get(Posisi).getHarga_Kost_Bulan().equals("0") && arrayDataKost.get(Posisi).getHarga_Kost_Tahun().equals("0"))){
                    Objects.requireNonNull(LLKonfirmasiSewaKostPer).setVisibility(View.GONE);
                }
                else if(arrayDataKost.get(Posisi).getHarga_Kost_Hari().equals("0")){
                    Objects.requireNonNull(RBKonfirmasiSewaKostHari).setVisibility(View.GONE);
                }
                else if(arrayDataKost.get(Posisi).getHarga_Kost_Tahun().equals("0")){
                    Objects.requireNonNull(RBKonfirmasiSewaKostTahun).setVisibility(View.GONE);
                }

                Objects.requireNonNull(RGKonfirmasiSewaKost).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(Objects.requireNonNull(RBKonfirmasiSewaKostHari).isChecked()){
                            txtKonfirmasiHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Hari());
                            SewaKost    = "Hari";
                        }
                        else if(Objects.requireNonNull(RBKonfirmasiSewaKostBulan).isChecked()){
                            txtKonfirmasiHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Bulan());
                            SewaKost    = "Bulan";
                        }
                        else if(Objects.requireNonNull(RBKonfirmasiSewaKostTahun).isChecked()){
                            txtKonfirmasiHargaKost.setText(arrayDataKost.get(Posisi).getHarga_Kost_Tahun());
                            SewaKost    = "Tahun";
                        }
                    }
                });

                RadioButton[] radioButtons  = new RadioButton[arrayDataKost.get(Posisi).getNama_Kamar().size()];
                ArrayList<String> txt       = arrayDataKost.get(Posisi).getNama_Kamar();

                Objects.requireNonNull(RGKonfirmasiNamaKamar).removeAllViews();

                for(int i = 0; i < radioButtons.length; i++){
                    radioButtons[i]                         = new RadioButton(BSD.getContext());
                    String[] txtRB                          = txt.get(i).split("=>");
                    RadioGroup.LayoutParams layoutParams    = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMarginEnd(5);
                    radioButtons[i].setText(txtRB[0]);
                    radioButtons[i].setLayoutParams(layoutParams);
                    radioButtons[i].setPadding(7,5,7, 5);
                    radioButtons[i].setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));

                    if(txtRB[1].equals("Kosong")){
                        radioButtons[i].setEnabled(false);
                        radioButtons[i].setTextColor(getColor(R.color.putih));
                        radioButtons[i].setBackground(new ColorDrawable(getColor(R.color.abuabu)));
                    }
                    else{
                        radioButtons[i].setEnabled(true);
                        radioButtons[i].setTextColor(getColor(R.color.color_button));
                        radioButtons[i].setBackground(getDrawable(R.drawable.bg_radiobutton_selector));
                    }

                    RGKonfirmasiNamaKamar.addView(radioButtons[i]);
                }

                PushDownAnim.setPushDownAnimTo(Objects.requireNonNull(btnKonfirmasiPesanKamar));
                btnKonfirmasiPesanKamar.setEnabled(false);
                btnKonfirmasiPesanKamar.setBackground(ContextCompat.getDrawable(ActivitySelengkapnya.this, R.color.abuabu));

                RGKonfirmasiNamaKamar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        btnKonfirmasiPesanKamar.setEnabled(true);
                        btnKonfirmasiPesanKamar.setBackground(ContextCompat.getDrawable(ActivitySelengkapnya.this, R.color.colorPrimary));
                        NamaKamar = ((RadioButton) Objects.requireNonNull(BSD.findViewById(RGKonfirmasiNamaKamar.getCheckedRadioButtonId()))).getText().toString();
                    }
                });

                btnKonfirmasiPesanKamar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @SuppressLint("NewApi")
                    public void onClick(View v) {
                        BSD.setCancelable(false);
                        btnKonfirmasiPesanKamar.setEnabled(false);
                        btnKonfirmasiPesanKamar.setText(R.string.Memproses_Pemesanan);
                        btnKonfirmasiPesanKamar.setBackground(ContextCompat.getDrawable(ActivitySelengkapnya.this, R.color.abuabu));

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan");
                        databaseReference.orderByChild("Email_Pemesan").equalTo(SP.getString("EmailAkun", ""))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        if(!String.valueOf(dataSnapshot.child("DataKost").child("ID_Kost").getValue()).equals(IDKost)){
                                            prosesPesanan(txtKonfirmasiHargaKost.getText().toString(), btnKonfirmasiPesanKamar);
                                        }
                                        else{
//                                            if(String.valueOf(dataSnapshot.child("Status_Pemesanan").getValue()).equals("Pesananan di Batalkan"))
                                            SP.edit().putString("MenuBawah", "Pemesanan").apply();
                                            SP.edit().putString("KeluarAplikasi", "Ya").apply();

                                            startActivity(new Intent(ActivitySelengkapnya.this, ActivityMain.class));
                                            startActivity(new Intent(ActivitySelengkapnya.this, ActivityPemesanan.class));
                                            finishAfterTransition();
                                        }
                                    }
                                }
                                else{
                                    prosesPesanan(txtKonfirmasiHargaKost.getText().toString(), btnKonfirmasiPesanKamar);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                });
            }
        });
    }

    public void prosesPesanan(String HargaKost, final Button btnKonfirmasiPesanKamar){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pemesanan").push();

        Map<String, Object> mapPemesanan = new HashMap<>();
        mapPemesanan.put("Nomor_Pemesanan", IDKost + new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(new Date()));
        mapPemesanan.put("Nama_Pemesan", SP.getString("NamaAkun", ""));
        mapPemesanan.put("Email_Pemesan", SP.getString("EmailAkun", ""));
        mapPemesanan.put("Nomor_Pembayaran", "");
        mapPemesanan.put("Metode_Pembayaran", "");
        mapPemesanan.put("Status_Pemesanan", "Menunggu Pembayaran");
        mapPemesanan.put("Tanggal_Masuk", new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date()));

        Map<String, Object> mapDataKost = new HashMap<>();
        mapDataKost.put("ID_Kost", IDKost);
        mapDataKost.put("Nama_Kost", txtNamaKost.getText().toString());
        mapDataKost.put("Harga_Kost", HargaKost);
        mapDataKost.put("Sewa_Per", SewaKost);
        mapDataKost.put("Nama_Kamar", NamaKamar);

        databaseReference.setValue(mapPemesanan);
        databaseReference.child("DataKost").setValue(mapDataKost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost").child(IDKost).child("Data_Umum_Kost").child("Nama_Kamar");
                SP.edit().putString("MenuBawah", "Pemesanan").apply();
                SP.edit().putString("KeluarAplikasi", "Ya").apply();

                startActivity(new Intent(ActivitySelengkapnya.this, ActivityMain.class));
                startActivity(new Intent(ActivitySelengkapnya.this, ActivityPemesanan.class));
                finishAfterTransition();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnKonfirmasiPesanKamar.setEnabled(true);
                btnKonfirmasiPesanKamar.setBackground(ContextCompat.getDrawable(ActivitySelengkapnya.this, R.color.colorPrimary));
            }
        });
    }
}