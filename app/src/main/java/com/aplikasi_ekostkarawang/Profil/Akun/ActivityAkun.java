package com.aplikasi_ekostkarawang.Profil.Akun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aplikasi_ekostkarawang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityAkun extends AppCompatActivity {
    Toolbar toolbarUbahProfil;
    ImageButton IBSimpanUbahProfil;
    ProgressBar PBSimpanUbahProfil;
    CircleImageView CIVFotoProfilAkun;
    LinearLayout LLNamaLengkap, LLNomorTeleponAkun, LLJenisKelaminAkun, LLPasswordAkun;
    TextView txtNamaLengkapAkun, txtEmailAkun, txtNomorTeleponAkun, txtJenisKelaminAkun;
    EditText txtUbahNamaLengkapAkun, txtUbahNomorTeleponAkun;
    Spinner spinnerJenisKelaminAkun;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    SharedPreferences SP;
    InputMethodManager inputMethodManager;

    Uri FotoProfil;
    boolean UbahFoto;
    static String Password;
    String MasukSebagai, IDAkun;
    ArrayList<String> JenisKelamin = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        toolbarUbahProfil       = findViewById(R.id.toolbarUbahProfil);
        IBSimpanUbahProfil      = findViewById(R.id.IBSimpanUbahProfil);
        PBSimpanUbahProfil      = findViewById(R.id.PBSimpanUbahProfil);
        CIVFotoProfilAkun       = findViewById(R.id.CIVFotoProfilAkun);
        LLNamaLengkap           = findViewById(R.id.LLNamaLengkap);
        LLNomorTeleponAkun      = findViewById(R.id.LLNomorTeleponAkun);
        LLJenisKelaminAkun      = findViewById(R.id.LLJenisKelaminAkun);
        LLPasswordAkun          = findViewById(R.id.LLPasswordAkun);
        txtNamaLengkapAkun      = findViewById(R.id.txtNamaLengkapAkun);
        txtEmailAkun            = findViewById(R.id.txtEmailAkun);
        txtNomorTeleponAkun     = findViewById(R.id.txtNomorTeleponAkun);
        txtJenisKelaminAkun     = findViewById(R.id.txtJenisKelaminAkun);
        txtUbahNamaLengkapAkun  = findViewById(R.id.txtUbahNamaLengkapAkun);
        txtUbahNomorTeleponAkun = findViewById(R.id.txtUbahNomorTeleponAkun);
        spinnerJenisKelaminAkun = findViewById(R.id.spinnerJenisKelaminAkun);

        SP                      = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        MasukSebagai            = SP.getString("MasukSebagai", "");
        IDAkun                  = SP.getString("IDAkun", "");
        inputMethodManager      = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        firebaseUser            = FirebaseAuth.getInstance().getCurrentUser();

        mengambilProfilAkun();
        menyimpanProfilAkun();
        setSupportActionBar(toolbarUbahProfil);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    public void mengambilProfilAkun(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(MasukSebagai).child(IDAkun);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!String.valueOf(snapshot.child("Foto_Profil").getValue()).isEmpty()){
                    Picasso.get().load(String.valueOf(snapshot.child("Foto_Profil").getValue())).placeholder(R.drawable.icon_profil).into(CIVFotoProfilAkun);
                }

                txtNamaLengkapAkun.setText(String.valueOf(snapshot.child("Nama_Lengkap").getValue()));
                txtEmailAkun.setText(String.valueOf(snapshot.child("Email").getValue()));
                txtNomorTeleponAkun.setText(String.valueOf(snapshot.child("Nomor_Telepon").getValue()));
                txtJenisKelaminAkun.setText(String.valueOf(snapshot.child("Jenis_Kelamin").getValue()));

                UbahFoto    = false;
                Password    = String.valueOf(snapshot.child("Password").getValue());

                mengubahProfilAkun();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void mengubahProfilAkun(){
        CIVFotoProfilAkun.setOnClickListener(v -> mengubahFotoProfilAkun());

        mengubahNamaLengkapAkun();
        mengubahNomorTeleponAkun();
        mengubahJenisKelaminAkun();

        LLPasswordAkun.setOnClickListener(v -> {
            startActivity(new Intent(ActivityAkun.this, ActivityPassword.class).putExtra("Password", Password));
            overridePendingTransition(R.anim.kanan_masuk, R.anim.kiri_keluar);
        });
    }

    public void mengubahFotoProfilAkun(){
        if(ContextCompat.checkSelfPermission(ActivityAkun.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(ActivityAkun.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(ActivityAkun.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(ActivityAkun.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityAkun.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(ActivityAkun.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                ActivityCompat.requestPermissions(ActivityAkun.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            else{
                ActivityCompat.requestPermissions(ActivityAkun.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else{
            CropImage.activity()
                    .setAllowFlipping(false)
                    .setAllowRotation(false)
                    .setAutoZoomEnabled(false)
                    .setBackgroundColor(Color.BLACK)
                    .setMultiTouchEnabled(true)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setCropMenuCropButtonIcon(R.drawable.icon_check)
                    .start(ActivityAkun.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED){
            mengubahFotoProfilAkun();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CIVFotoProfilAkun.setImageURI(Objects.requireNonNull(CropImage.getActivityResult(data)).getUri());

            UbahFoto    = true;
            FotoProfil  = CropImage.getActivityResult(data).getUri();
        }
    }

    public void mengubahNamaLengkapAkun(){
        LLNamaLengkap.setOnClickListener(v -> {
            visibilityView(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);

            txtUbahNamaLengkapAkun.requestFocus();
            txtUbahNamaLengkapAkun.setText(txtNamaLengkapAkun.getText().toString());
            txtUbahNamaLengkapAkun.setSelection(txtNamaLengkapAkun.getText().length());
        });

        txtUbahNamaLengkapAkun.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                txtNamaLengkapAkun.setText(txtUbahNamaLengkapAkun.getText().toString());
                visibilityView(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
                return true;
            }
            return false;
        });
    }

    public void mengubahNomorTeleponAkun(){
        LLNomorTeleponAkun.setOnClickListener(v -> {
            visibilityView(View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);

            txtUbahNomorTeleponAkun.requestFocus();
            txtUbahNomorTeleponAkun.setText(txtNomorTeleponAkun.getText().toString());
            txtUbahNomorTeleponAkun.setSelection(txtNomorTeleponAkun.getText().length());
        });

        txtUbahNomorTeleponAkun.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                txtNomorTeleponAkun.setText(txtUbahNomorTeleponAkun.getText().toString());
                visibilityView(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
                return true;
            }
            return false;
        });
    }

    public void mengubahJenisKelaminAkun(){
        JenisKelamin.clear();
        JenisKelamin.add(getString(R.string.pria));
        JenisKelamin.add(getString(R.string.wanita));

        spinnerJenisKelaminAkun.setPadding(0, 0, 0, 0);
        spinnerJenisKelaminAkun.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, JenisKelamin));

        LLJenisKelaminAkun.setOnClickListener(v -> {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            visibilityView(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
        });

        spinnerJenisKelaminAkun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                txtJenisKelaminAkun.setText(spinnerJenisKelaminAkun.getSelectedItem().toString());
                visibilityView(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void visibilityView(int NamaLengkap, int UbahNamaLengkap, int NomorTelepon, int UbahNomorTelepon, int JenisKelamin, int UbahJenisKelamin){
        txtNamaLengkapAkun.setVisibility(NamaLengkap);
        txtNomorTeleponAkun.setVisibility(NomorTelepon);
        txtJenisKelaminAkun.setVisibility(JenisKelamin);
        txtUbahNamaLengkapAkun.setVisibility(UbahNamaLengkap);
        txtUbahNomorTeleponAkun.setVisibility(UbahNomorTelepon);
        spinnerJenisKelaminAkun.setVisibility(UbahJenisKelamin);
    }

    public void menyimpanProfilAkun(){
        IBSimpanUbahProfil.setOnClickListener(v -> {
            visibilityView(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            PBSimpanUbahProfil.setVisibility(View.VISIBLE);
            IBSimpanUbahProfil.setVisibility(View.GONE);

            HashMap<String, Object> ProfilAkun = new HashMap<>();
            ProfilAkun.put("Nama_Lengkap", txtNamaLengkapAkun.getText().toString());
            ProfilAkun.put("Nomor_Telepon", txtNomorTeleponAkun.getText().toString());
            ProfilAkun.put("Jenis_Kelamin", txtJenisKelaminAkun.getText().toString());

            if(UbahFoto){
                storageReference = FirebaseStorage.getInstance().getReference().child("FotoProfil").child(IDAkun + ".jpg");
                storageReference.putFile(FotoProfil).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    ProfilAkun.put("Foto_Profil", uri.toString());
                    FirebaseDatabase.getInstance().getReference().child("Pengguna").child(MasukSebagai).child(IDAkun).updateChildren(ProfilAkun);

                    UserProfileChangeRequest UPCR = new UserProfileChangeRequest.Builder()
                            .setDisplayName(txtNamaLengkapAkun.getText().toString())
                            .setPhotoUri(Uri.parse(uri.toString()))
                            .build();
                    firebaseUser.updateProfile(UPCR);
                    supportFinishAfterTransition();
                }));
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Pengguna").child(MasukSebagai).child(IDAkun).updateChildren(ProfilAkun);

                UserProfileChangeRequest UPCR = new UserProfileChangeRequest.Builder()
                        .setDisplayName(txtNamaLengkapAkun.getText().toString())
                        .build();
                firebaseUser.updateProfile(UPCR);
                supportFinishAfterTransition();
            }
        });
    }
}