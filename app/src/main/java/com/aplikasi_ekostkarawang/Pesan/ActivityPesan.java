package com.aplikasi_ekostkarawang.Pesan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aplikasi_ekostkarawang.Beranda.AdapterDataKost;
import com.aplikasi_ekostkarawang.Beranda.ArrayDataKost;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Client;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Data;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Jawaban;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Pengirim;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Token;
import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPesan extends AppCompatActivity {
    Toolbar toolbarPesan;
    CircleImageView CIVPesan;
    TextView txtNamaPemilikKostPesan, txtTerakhirAktifPesan;
    RecyclerView RVPesan;
    EditText txtKirimPesan;
    ImageButton IBPesan, IBPesanKamera, IBPesanGaleri;

    DatabaseReference databaseReference;
    SharedPreferences SP;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    ValueEventListener pesanTerkirimListener;

    AdapterPesan adapterPesan;
    LayananAPI layananAPI;

    int Posisi;
    Uri fileUri;
    String FotoPemilik, NamaPemilik;
    ArrayList<ArrayDataKost> arrayDataKost  = new ArrayList<>();
    ArrayList<ArrayPesan> arrayPesan        = new ArrayList<>();
    boolean notify                          = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);

        toolbarPesan            = findViewById(R.id.toolbarPesan);
        CIVPesan                = findViewById(R.id.CIVPesan);
        txtNamaPemilikKostPesan = findViewById(R.id.txtNamaPemilikKostPesan);
        txtTerakhirAktifPesan   = findViewById(R.id.txtTerakhirAktifPesan);
        RVPesan                 = findViewById(R.id.RVPesan);
        txtKirimPesan           = findViewById(R.id.txtKirimPesan);
        IBPesanKamera           = findViewById(R.id.IBPesanKamera);
        IBPesanGaleri           = findViewById(R.id.IBPesanGaleri);
        IBPesan                 = findViewById(R.id.IBPesan);

        arrayDataKost           = AdapterDataKost.arrayDataKost;
        Posisi                  = Objects.requireNonNull(getIntent().getExtras()).getInt("Posisi");
        FotoPemilik             = Objects.requireNonNull(getIntent().getExtras()).getString("FotoPemilik");
        NamaPemilik             = Objects.requireNonNull(getIntent().getExtras()).getString("NamaPemilik");
        SP                      = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        firebaseUser            = FirebaseAuth.getInstance().getCurrentUser();
        layananAPI              = Client.getRetrofit("https://fcm.googleapis.com/").create(LayananAPI.class);

        txtNamaPemilikKostPesan.setText(NamaPemilik);
        txtTerakhirAktifPesan.setVisibility(View.GONE);
        Glide.with(this).asBitmap().load(FotoPemilik).into(CIVPesan);
        IBPesan.setEnabled(false);

        LinearLayoutManager LLM = new LinearLayoutManager(getApplicationContext());
        LLM.setStackFromEnd(true);
        RVPesan.setLayoutManager(LLM);
        RVPesan.setHasFixedSize(true);
        RVPesan.setItemAnimator(new DefaultItemAnimator());

        kirimPesan();
        terakhirAktif();
        kirimGambarPesan();
        pesanTerkirim(arrayDataKost.get(Posisi).getPemilik());
        bacaPesan(arrayDataKost.get(Posisi).getPemilik(), SP.getString("EmailAkun", ""), FotoPemilik);
        setSupportActionBar(toolbarPesan);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PushDownAnim.setPushDownAnimTo(IBPesan, IBPesanKamera, IBPesanGaleri);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    public void kirimGambarPesan(){
        IBPesanKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.TITLE, "Gambar Baru");
                contentValues.put(MediaStore.Images.Media.DESCRIPTION, "dari Kamera Anda");

                fileUri         = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Intent intent   = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 100);

            }
        });

        IBPesanGaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 10){
            if(resultCode == RESULT_OK){
                try{
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    fileUri = uri;
                }
                catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void terakhirAktif(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik");
                databaseReference.orderByChild("Email").equalTo(arrayDataKost.get(Posisi).getPemilik()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String Status   = String.valueOf(dataSnapshot.child("Status_Pengguna").child("Status").getValue());
                            String Waktu   = String.valueOf(dataSnapshot.child("Status_Pengguna").child("Waktu").getValue());

                            if(Status.equals("Online")){
                                txtTerakhirAktifPesan.setText(R.string.online);
                            }
                            else{
                                txtTerakhirAktifPesan.setText(TimeAgo.getTimeAgo(Long.parseLong(Waktu)));
                            }

                            txtTerakhirAktifPesan.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public void kirimPesan(){
        txtKirimPesan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtKirimPesan.getText().toString().equals("")){
                    IBPesan.setEnabled(false);
                }
                else{
                    IBPesan.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        IBPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                pesan(SP.getString("EmailAkun", ""), arrayDataKost.get(Posisi).getPemilik(), txtKirimPesan.getText().toString());
                txtKirimPesan.getText().clear();
            }
        });
    }

    public void pesanTerkirim(final String Email){
        databaseReference       = FirebaseDatabase.getInstance().getReference().child("Pesan");
        pesanTerkirimListener   = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ArrayPesan Pesan = dataSnapshot.getValue(ArrayPesan.class);
                    if(Pesan.getPenerima().equals(firebaseUser.getEmail()) && Pesan.getPengirim().equals(Email)){
                        HashMap<String, Object> terkirim = new HashMap<>();
                        terkirim.put("Terkirim", true);
                        dataSnapshot.getRef().updateChildren(terkirim);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void pesan(String Pengirim, String Penerima, String Pesan){
        Calendar calendar   = Calendar.getInstance();
        String waktuPesan   = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());
        String tanggalPesan = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
        databaseReference   = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> kirimPesan = new HashMap<>();
        kirimPesan.put("Pengirim", Pengirim);
        kirimPesan.put("Penerima", Penerima);
        kirimPesan.put("Pesan", Pesan);
        kirimPesan.put("Waktu", waktuPesan);
        kirimPesan.put("Tanggal", tanggalPesan);
        kirimPesan.put("TimeStamp", System.currentTimeMillis());
        kirimPesan.put("Terkirim", false);

        databaseReference.child("Pesan").push().setValue(kirimPesan);

//        if(notify){
//            kirimNotifikasi(Penerima, Pengirim, Pesan);
//        }
//        notify = false;
    }

    public void kirimNotifikasi(String Penerima, final String Email, final String Pesan){
        databaseReference   = FirebaseDatabase.getInstance().getReference().child("Token");
        Query query         = databaseReference.orderByKey().equalTo(Penerima);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data   = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, Email + " : " + Pesan, "Pesan Baru", Email);

                    Pengirim pengirim = new Pengirim(data, token.getToken());

                    layananAPI.kirimNotifikasi(pengirim)
                            .enqueue(new Callback<Jawaban>() {
                                @Override
                                public void onResponse(Call<Jawaban> call, Response<Jawaban> response) {
                                    if(response.code() == 200){
                                        if(response.body().Sukses != 1){
                                            Toast.makeText(ActivityPesan.this, "Gagal !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Jawaban> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void bacaPesan(final String Penerima, final String Pengirim, final String imgURL){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pesan");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayPesan.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ArrayPesan Pesan = dataSnapshot.getValue(ArrayPesan.class);

                    if(Pesan.getPenerima().equals(Penerima) && Pesan.getPengirim().equals(Pengirim) ||
                            Pesan.getPenerima().equals(Pengirim) && Pesan.getPengirim().equals(Penerima)){
                        arrayPesan.add(Pesan);
                    }

                    adapterPesan = new AdapterPesan(ActivityPesan.this, arrayPesan, imgURL);
                    RVPesan.setAdapter(adapterPesan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void statusPengguna(final String Status){
        final HashMap<String, Object> statusPengguna = new HashMap<>();
        statusPengguna.put("Waktu", System.currentTimeMillis());
        statusPengguna.put("Status", Status);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
        databaseReference.orderByChild("Email").equalTo(SP.getString("EmailAkun", "")).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    protected void onResume() {
        super.onResume();

        statusPengguna("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        databaseReference.removeEventListener(pesanTerkirimListener);
        statusPengguna("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        statusPengguna("Offline");
    }
}

class TimeAgo{
    private static final int SECOND_MILLIS  = 1000;
    private static final int MINUTE_MILLIS  = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS    = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS     = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS    = 7 * DAY_MILLIS;
    private static final int MONTH_MILIS    = 4 * WEEK_MILLIS;

    static String getTimeAgo(long Time){
        if(Time < 1000000000000L){
            Time *= 1000;
        }

        long now = System.currentTimeMillis();
        if(Time > now || Time <= 0){
            return null;
        }

        final long diff = now - Time;
        if(diff < MINUTE_MILLIS){
            return "Aktif " + diff / SECOND_MILLIS + " detik yang lalu";
        }
        else if(diff < 2 * MINUTE_MILLIS){
            return "Aktif semenit yang lalu";
        }
        else if(diff < 50 * MINUTE_MILLIS){
            return "Aktif " + diff / MINUTE_MILLIS + " menit yang lalu";
        }
        else if(diff < 90 * MINUTE_MILLIS){
            return "Aktif sejam yang lalu";
        }
        else if(diff < 24 * HOUR_MILLIS){
            return "Aktif " + diff / HOUR_MILLIS + " jam yang lalu";
        }
        else if(diff < 48 * HOUR_MILLIS){
            return "Aktif kemarin";
        }
        else if(diff < 7 * DAY_MILLIS){
            return "Aktif " + diff / DAY_MILLIS + " hari yang lalu";
        }
        else if(diff < 2 * WEEK_MILLIS){
            return "Aktif seminggu yang lalu";
        }
        else{
            return "Aktif " + diff / WEEK_MILLIS + " minggu yang lalu";
        }
    }
}