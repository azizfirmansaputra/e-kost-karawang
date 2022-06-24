package com.aplikasi_ekostkarawang.Beranda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.Beranda.Favorite.ActivityFavorite;
import com.aplikasi_ekostkarawang.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FragmentBeranda extends Fragment {
    private ImageView imgFavorite;
    private SwipeRefreshLayout SRLBeranda;
    private ScrollView SVBeranda;
    private AutoCompleteTextView txtCariLokasiKost;
    private TextView txtListKostTerdekatDari;
    private LinearLayout LLKostTerdekatDari;
    private Button btnCariLokasiKost, btnKIIC, btnSuryaCipta, btnMitraKarawang;
    private RecyclerView RVKostBeranda;
    private ShimmerFrameLayout SFLListKostBeranda;
    private RelativeLayout RLKostTidakAda;

    private FusedLocationProviderClient FLPC;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private SharedPreferences SP;
    private LatLng lokasiTerakhir;
    private InputMethodManager IMM;

    private AdapterDataKost adapterDataKost;
    private AdapterCariKost adapterCariKost;
    private ArrayList<ArrayDataKost> arrayCariKost  = new ArrayList<>();
    private ArrayList<ArrayDataKost> arrayKostDekat = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view               = inflater.inflate(R.layout.fragment_beranda, container, false);

        imgFavorite             = view.findViewById(R.id.imgFavorite);
        SRLBeranda              = view.findViewById(R.id.SRLBeranda);
        SVBeranda               = view.findViewById(R.id.SVBeranda);
        txtCariLokasiKost       = view.findViewById(R.id.txtCariLokasiKost);
        txtListKostTerdekatDari = view.findViewById(R.id.txtListKostTerdekatDari);
        LLKostTerdekatDari      = view.findViewById(R.id.LLKostTerdekatDari);
        btnCariLokasiKost       = view.findViewById(R.id.btnCariLokasiKost);
        btnKIIC                 = view.findViewById(R.id.btnKIIC);
        btnSuryaCipta           = view.findViewById(R.id.btnSuryaCipta);
        btnMitraKarawang        = view.findViewById(R.id.btnMitraKarawang);
        RVKostBeranda           = view.findViewById(R.id.RVKostBeranda);
        SFLListKostBeranda      = view.findViewById(R.id.SFLListKostBeranda);
        RLKostTidakAda          = view.findViewById(R.id.RLKostTidakAda);

        FLPC                    = LocationServices.getFusedLocationProviderClient(requireContext());
        SP                      = requireContext().getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);
        IMM                     = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        lokasiTerakhir          = new LatLng(SP.getFloat("Latitude", 0), SP.getFloat("Longitude", 0));
        adapterCariKost         = new AdapterCariKost(requireContext(), R.layout.list_favorite, arrayCariKost);
        adapterDataKost         = new AdapterDataKost(getContext(), arrayKostDekat);

        FirebaseAuth FA         = FirebaseAuth.getInstance();
        firebaseUser            = FA.getCurrentUser();

        arrayCariKost.clear();
        arrayKostDekat.clear();
        txtCariLokasiKost.setThreshold(1);
        txtCariLokasiKost.setAdapter(adapterCariKost);
        SFLListKostBeranda.startShimmerAnimation();
        RVKostBeranda.setHasFixedSize(true);
        RVKostBeranda.setVisibility(View.GONE);
        RVKostBeranda.setAdapter(adapterDataKost);
        RVKostBeranda.setItemAnimator(new DefaultItemAnimator());
        RVKostBeranda.setLayoutManager(new LinearLayoutManager(getContext()));
        RVKostBeranda.addItemDecoration(new DividerItemDecoration(RVKostBeranda.getContext(), LinearLayoutManager.VERTICAL));

        refreshBeranda();
        cekKoneksiInternet();
        listKostFavorite();
        cariLokasiKost();
        cariKostTerdekat();
        PushDownAnim.setPushDownAnimTo(imgFavorite, btnCariLokasiKost, btnKIIC, btnSuryaCipta, btnMitraKarawang);

        return view;
    }

    private void refreshBeranda(){
        SRLBeranda.setColorSchemeColors(requireContext().getColor(R.color.colorPrimary));
        SRLBeranda.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayCariKost.clear();
                arrayKostDekat.clear();
                cekKoneksiInternet();
                cariKostTerdekat();
            }
        });
    }

    private void cekKoneksiInternet(){
        ConnectivityManager CM = (ConnectivityManager)requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            NetworkCapabilities NC = Objects.requireNonNull(CM).getNetworkCapabilities(CM.getActiveNetwork());

            if(NC != null){
                cekLokasiTerakhir();
            }
            else{
                snackBar(getString(R.string.Tidak_Ada_Koneksi_Internet));
            }
        }
        else{
            NetworkInfo NI = Objects.requireNonNull(CM).getActiveNetworkInfo();

            if(NI != null){
                cekLokasiTerakhir();
            }
            else{
                snackBar(getString(R.string.Tidak_Ada_Koneksi_Internet));
            }
        }
    }

    private void cekLokasiTerakhir(){
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Task<Location> task = FLPC.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    SP.edit().putFloat("Latitude", (float)location.getLatitude()).apply();
                    SP.edit().putFloat("Longitude", (float)location.getLongitude()).apply();
                    lokasiTerakhir = new LatLng(location.getLatitude(), location.getLongitude());
                    mengambilDataKost(false, lokasiTerakhir);
                }
            });
        }
    }

    private void snackBar(String pesan){
        if(getActivity() != null){
            Snackbar snackBar   = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), pesan, Snackbar.LENGTH_LONG);
            TextView textView   = snackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);

            textView.setTextColor(Color.RED);
            snackBar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.hitam_transparan));
            snackBar.show();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void listKostFavorite(){
        if(SP.getString("MasukSebagai", "").equals("Admin")){
            imgFavorite.setVisibility(View.GONE);
        }

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true);
        imgFavorite.setBackgroundResource(typedValue.resourceId);

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMM.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(firebaseUser == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog)
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
                                    startActivity(new Intent(getActivity(), ActivityMain.class));
                                    requireActivity().finish();
                                    dialog.dismiss();
                                }
                            });
                    builder.setCancelable(true);
                    builder.show();
                }
                else{
                    startActivity(new Intent(getActivity(), ActivityFavorite.class));
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cariLokasiKost(){
        txtCariLokasiKost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    SRLBeranda.setEnabled(false);
                    SVBeranda.smoothScrollTo(0, txtCariLokasiKost.getTop());
                    IMM.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });

        SVBeranda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(txtCariLokasiKost.isFocused()){
                        Rect rect = new Rect();
                        txtCariLokasiKost.getGlobalVisibleRect(rect);

                        if(!rect.contains((int)event.getRawX(), (int)event.getRawY())){
                            SRLBeranda.setEnabled(true);
                            txtCariLokasiKost.clearFocus();
                            IMM.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

        txtCariLokasiKost.addTextChangedListener(TWCariLokasiKost);
        txtCariLokasiKost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((ArrayDataKost)parent.getItemAtPosition(position)).getNama_Kost().equals(getString(R.string.Cari_Kost_Dekat_saya))){
                    arrayKostDekat.clear();
                    txtCariLokasiKost.getText().clear();
                    mengambilDataKost(false, lokasiTerakhir);
                    IMM.hideSoftInputFromWindow(txtCariLokasiKost.getWindowToken(), 0);
                }
                else{
                    txtCariLokasiKost.setText(((ArrayDataKost)parent.getItemAtPosition(position)).getNama_Kost());
                    txtCariLokasiKost.setSelection(txtCariLokasiKost.getText().length());
                }
            }
        });

        txtCariLokasiKost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!txtCariLokasiKost.getText().toString().isEmpty()){
                        prosesCariKost(v);
                    }
                }
                return false;
            }
        });

        btnCariLokasiKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtCariLokasiKost.getText().toString().isEmpty()){
                    prosesCariKost(v);
                }
            }
        });
    }

    private TextWatcher TWCariLokasiKost = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            txtCariLokasiKost.removeTextChangedListener(TWCariLokasiKost);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayCariKost.add(new ArrayDataKost(getString(R.string.Cari_Kost_Dekat_saya), "", "", "", "", "",
                            "", "", "", "", "", "", "", "",null,
                            null, null, null, null, null));
                    adapterCariKost.notifyDataSetChanged();

                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String Nama_Kost            = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                            String Alamat_Kost          = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                            ArrayList<String> Foto_Kost = new ArrayList<>();

                            if(Nama_Kost.toLowerCase().contains(txtCariLokasiKost.getText().toString().toLowerCase())){
                                for(DataSnapshot DS : dataSnapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                                    Foto_Kost.add(String.valueOf(DS.getValue()));
                                }

                                arrayCariKost.add(new ArrayDataKost(Nama_Kost, "", "", "", "", "",
                                        "", "", "", "", "", "", Alamat_Kost, "",
                                        null, null, null, null, Foto_Kost, null));
                                adapterCariKost.notifyDataSetChanged();
                            }
                        }

                        txtCariLokasiKost.addTextChangedListener(TWCariLokasiKost);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void prosesCariKost(View v){
        arrayKostDekat.clear();
        RVKostBeranda.setVisibility(View.GONE);
        SFLListKostBeranda.startShimmerAnimation();
        SFLListKostBeranda.setVisibility(View.VISIBLE);
        mengambilDataKost(true, lokasiTerakhir);
        IMM.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void cariKostTerdekat(){
        kostTerdekat(R.color.hitam, R.color.hitam, R.color.hitam, R.drawable.bordireditteks, R.drawable.bordireditteks, R.drawable.bordireditteks);

        btnKIIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayKostDekat.clear();
                mengambilDataKost(false, new LatLng(-6.3588958197035765, 107.27421779911211));
                kostTerdekat(R.color.putih, R.color.hitam, R.color.hitam, R.drawable.bordirbuttonoutline, R.drawable.bordireditteks, R.drawable.bordireditteks);
            }
        });

        btnSuryaCipta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayKostDekat.clear();
                mengambilDataKost(false, new LatLng(-6.371723786333512, 107.32746576043138));
                kostTerdekat(R.color.hitam, R.color.putih, R.color.hitam, R.drawable.bordireditteks, R.drawable.bordirbuttonoutline, R.drawable.bordireditteks);
            }
        });

        btnMitraKarawang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayKostDekat.clear();
                mengambilDataKost(false, new LatLng(-6.381407711724465, 107.31062695428197));
                kostTerdekat(R.color.hitam, R.color.hitam, R.color.putih, R.drawable.bordireditteks, R.drawable.bordireditteks, R.drawable.bordirbuttonoutline);
            }
        });
    }

    private void kostTerdekat(int colorKIIC, int colorSuryaCipta, int colorMitraKarawang, int bgKIIC, int bgSuryaCipta, int bgMitraKarawang){
        btnKIIC.setTextColor(ContextCompat.getColor(requireContext(), colorKIIC));
        btnKIIC.setBackground(ContextCompat.getDrawable(requireContext(), bgKIIC));
        btnSuryaCipta.setTextColor(ContextCompat.getColor(requireContext(), colorSuryaCipta));
        btnSuryaCipta.setBackground(ContextCompat.getDrawable(requireContext(), bgSuryaCipta));
        btnMitraKarawang.setTextColor(ContextCompat.getColor(requireContext(), colorMitraKarawang));
        btnMitraKarawang.setBackground(ContextCompat.getDrawable(requireContext(), bgMitraKarawang));
    }

    private void mengambilDataKost(final boolean CariKost, final LatLng lokasiTerdekat){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("DataKost");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            @SuppressLint("SetTextI18n")
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String Nama_Kost            = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Nama_Kost").getValue());
                        String Deskripsi_Kost       = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Deskripsi_Kost").getValue());
                        String Total_Kamar          = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Total_Kamar").getValue());
                        String Sisa_Kamar           = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Kapasitas_Kost").child("Sisa_Kamar").getValue());
                        String Mayoritas_Penghuni   = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Mayoritas_Penghuni").getValue());
                        String Harga_Kost_Hari      = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Hari").getValue());
                        String Harga_Kost_Bulan     = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Bulan").getValue());
                        String Harga_Kost_Tahun     = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Harga_Kost").child("Tahun").getValue());
                        String Peraturan_Kost       = String.valueOf(dataSnapshot.child("Data_Umum_Kost").child("Peraturan_Kost").getValue());
                        String Pemilik              = String.valueOf(dataSnapshot.child("Pemilik").getValue());
                        String Kecamatan            = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Kecamatan").getValue());
                        String Desa                 = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Desa").getValue());
                        String Alamat_Kost          = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Alamat_Kost").getValue());
                        String Latitude             = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Latitude").getValue());
                        String Longitude            = String.valueOf(dataSnapshot.child("Lokasi_Kost").child("Lokasi_Peta").child("Longitude").getValue());
                        LatLng latLng               = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                        double jarak                = Jarak(lokasiTerdekat, latLng);
                        String Jarak                = String.format(Locale.getDefault(), "%.2f", jarak) + getString(R.string.km);

                        ArrayList<String> Nama_Kamar        = new ArrayList<>();
                        ArrayList<String> Fasilitas_Bersama = new ArrayList<>();
                        ArrayList<String> Fasilitas_Sekitar = new ArrayList<>();
                        ArrayList<String> Dekat_Dengan      = new ArrayList<>();
                        ArrayList<String> Foto_Kost         = new ArrayList<>();
                        ArrayList<String> Video_Kost        = new ArrayList<>();

                        for(DataSnapshot DS : dataSnapshot.child("Data_Umum_Kost").child("Nama_Kamar").getChildren()){
                            Nama_Kamar.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Fasilitas_Bersama").getChildren()){
                            Fasilitas_Bersama.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Fasilitas_Sekitar").getChildren()){
                            Fasilitas_Sekitar.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Dekat_Dengan").getChildren()){
                            Dekat_Dengan.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Foto_Video_Kost").child("Foto").getChildren()){
                            Foto_Kost.add(String.valueOf(DS.getValue()));
                        }

                        for(DataSnapshot DS : dataSnapshot.child("Foto_Video_Kost").child("Video").getChildren()){
                            Video_Kost.add(String.valueOf(DS.getValue()));
                        }

                        if(CariKost){
                            if(Nama_Kost.toLowerCase().contains(txtCariLokasiKost.getText().toString().toLowerCase())){
                                arrayKostDekat.add(new ArrayDataKost(Nama_Kost, Deskripsi_Kost, Total_Kamar, Sisa_Kamar, Mayoritas_Penghuni, Harga_Kost_Hari,
                                        Harga_Kost_Bulan, Harga_Kost_Tahun, Peraturan_Kost, Pemilik, Kecamatan, Desa, Alamat_Kost, Jarak, Nama_Kamar, Fasilitas_Bersama,
                                        Fasilitas_Sekitar, Dekat_Dengan, Foto_Kost, Video_Kost));
                                adapterDataKost.notifyDataSetChanged();

                                LLKostTerdekatDari.setVisibility(View.GONE);
                                txtListKostTerdekatDari.setText(getString(R.string.Pencarian_Kost_) + txtCariLokasiKost.getText().toString());
                                txtCariLokasiKost.getText().clear();
                            }
                        }
                        else{
                            if(jarak <= 3.0){
                                arrayKostDekat.add(new ArrayDataKost(Nama_Kost, Deskripsi_Kost, Total_Kamar, Sisa_Kamar, Mayoritas_Penghuni, Harga_Kost_Hari,
                                        Harga_Kost_Bulan, Harga_Kost_Tahun, Peraturan_Kost, Pemilik, Kecamatan, Desa, Alamat_Kost, Jarak, Nama_Kamar, Fasilitas_Bersama,
                                        Fasilitas_Sekitar, Dekat_Dengan, Foto_Kost, Video_Kost));
                                adapterDataKost.notifyDataSetChanged();

                                txtCariLokasiKost.getText().clear();
                                LLKostTerdekatDari.setVisibility(View.VISIBLE);
                                txtListKostTerdekatDari.setText(getString(R.string.lihat_kost_terdekat_dari));
                            }
                        }

                        SRLBeranda.setRefreshing(false);
                        SFLListKostBeranda.setVisibility(View.GONE);
                        SFLListKostBeranda.stopShimmerAnimation();
                        RVKostBeranda.setVisibility(View.VISIBLE);
                        RLKostTidakAda.setVisibility(View.GONE);
                    }

                    if(arrayKostDekat.size() == 0){
                        RVKostBeranda.setVisibility(View.GONE);
                        RLKostTidakAda.setVisibility(View.VISIBLE);

                        txtListKostTerdekatDari.setText(getString(R.string.Pencarian_Kost_) + txtCariLokasiKost.getText().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
}