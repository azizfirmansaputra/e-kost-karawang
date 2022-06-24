package com.aplikasi_ekostkarawang.Profil.Akun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aplikasi_ekostkarawang.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityPassword extends AppCompatActivity {
    Toolbar toolbarUbahPassword;
    ImageButton IBSimpanUbahPassword;
    ProgressBar PBSimpanUbahPassword;
    TextView txtPasswordError;
    TextInputLayout TILPasswordLama;
    TextInputEditText txtPasswordLama, txtPasswordBaru, txtPasswordBaruUlangi;

    DatabaseReference databaseReference;
    SharedPreferences SP;

    String PasswordLama, MasukSebagai, IDAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        toolbarUbahPassword     = findViewById(R.id.toolbarUbahPassword);
        IBSimpanUbahPassword    = findViewById(R.id.IBSimpanUbahPassword);
        PBSimpanUbahPassword    = findViewById(R.id.PBSimpanUbahPassword);
        txtPasswordError        = findViewById(R.id.txtPasswordError);
        TILPasswordLama         = findViewById(R.id.TILPasswordLama);
        txtPasswordLama         = findViewById(R.id.txtPasswordLama);
        txtPasswordBaru         = findViewById(R.id.txtPasswordBaru);
        txtPasswordBaruUlangi   = findViewById(R.id.txtPasswordBaruUlangi);

        SP                      = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        PasswordLama            = getIntent().getStringExtra("Password");
        MasukSebagai            = SP.getString("MasukSebagai", "");
        IDAkun                  = SP.getString("IDAkun", "");

        cekPassword();
        setSupportActionBar(toolbarUbahPassword);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            supportFinishAfterTransition();
            overridePendingTransition(R.anim.kanan_masuk, R.anim.kiri_keluar);
        }
        return super.onOptionsItemSelected(item);
    }

    public void cekPassword(){
        if(PasswordLama.isEmpty()){
            TILPasswordLama.setVisibility(View.GONE);
        }

        textChangeListener(txtPasswordLama);
        textChangeListener(txtPasswordBaru);
        textChangeListener(txtPasswordBaruUlangi);

        menyimpanPasswordBaru();
    }

    public void textChangeListener(TextInputEditText textInputEditText){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtPasswordError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void menyimpanPasswordBaru(){
        IBSimpanUbahPassword.setOnClickListener(v -> {
            if(!txtPasswordLama.getText().toString().equals(PasswordLama)){
                txtPasswordLama.requestFocus();
                txtPasswordLama.getText().clear();
                txtPasswordError.setVisibility(View.VISIBLE);
                txtPasswordError.setText(R.string.Password_Lama_Salah);
            }
            else if(txtPasswordBaru.getText().length() < 6){
                txtPasswordBaru.requestFocus();
                txtPasswordError.setVisibility(View.VISIBLE);
                txtPasswordError.setText(getString(R.string.Minimal_6_karakter_));
            }
            else if(!txtPasswordBaru.getText().toString().equals(txtPasswordBaruUlangi.getText().toString())){
                txtPasswordBaruUlangi.requestFocus();
                txtPasswordError.setVisibility(View.VISIBLE);
                txtPasswordError.setText(R.string.Password__Baru_Tidak_Sama);
            }
            else{
                IBSimpanUbahPassword.setVisibility(View.GONE);
                PBSimpanUbahPassword.setVisibility(View.VISIBLE);

                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(MasukSebagai).child(IDAkun);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ActivityAkun.Password = txtPasswordBaruUlangi.getText().toString();

                        snapshot.child("Password").getRef().setValue(txtPasswordBaruUlangi.getText().toString());
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(txtPasswordBaruUlangi.getText().toString());

                        supportFinishAfterTransition();
                        overridePendingTransition(R.anim.kanan_masuk, R.anim.kiri_keluar);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.kanan_masuk, R.anim.kiri_keluar);
    }
}