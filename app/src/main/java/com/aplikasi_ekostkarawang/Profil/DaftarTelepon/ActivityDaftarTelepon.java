package com.aplikasi_ekostkarawang.Profil.DaftarTelepon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ActivityDaftarTelepon extends AppCompatActivity {
    Toolbar toolbarNomorTelepon;
    TextView txtJudulDaftarNoTelepon, txtCountdownOTP, txtDaftarNoTeleponError;
    LinearLayout LLDaftarNoTelepon, LLKodeOTP, LLDaftarDataNoTelepon;
    EditText txtDaftarNoTelepon, txtKodeOTP;
    Button btnKirimOTP, btnDaftarNoTelepon;
    TextInputEditText txtDaftarNamaLengkap, txtDaftarEmail, txtDaftarPassword, txtDaftarKonfirmasiPassword;
    RadioGroup RGDaftarJenisKelamin;
    RadioButton RBDaftarLakiLaki, RBDaftarPerempuan;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks;
    InputMethodManager inputMethodManager;
    SharedPreferences SP;

    ProgressDialog progressDialog;

    String NoTelepon, verifikasiID, OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_telepon);

        toolbarNomorTelepon         = findViewById(R.id.toolbarNomorTelepon);
        txtJudulDaftarNoTelepon     = findViewById(R.id.txtJudulDaftarNoTelepon);
        txtCountdownOTP             = findViewById(R.id.txtCountdownOTP);
        txtDaftarNoTeleponError     = findViewById(R.id.txtDaftarNoTeleponError);
        LLDaftarNoTelepon           = findViewById(R.id.LLDaftarNoTelepon);
        LLKodeOTP                   = findViewById(R.id.LLKodeOTP);
        LLDaftarDataNoTelepon       = findViewById(R.id.LLDaftarDataNoTelepon);
        txtDaftarNoTelepon          = findViewById(R.id.txtDaftarNoTelepon);
        txtKodeOTP                  = findViewById(R.id.txtKodeOTP);
        btnKirimOTP                 = findViewById(R.id.btnKirimOTP);
        btnDaftarNoTelepon          = findViewById(R.id.btnDaftarNoTelepon);
        txtDaftarNamaLengkap        = findViewById(R.id.txtDaftarNamaLengkap);
        txtDaftarEmail              = findViewById(R.id.txtDaftarEmail);
        txtDaftarPassword           = findViewById(R.id.txtDaftarPassword);
        txtDaftarKonfirmasiPassword = findViewById(R.id.txtDaftarKonfirmasiPassword);
        RGDaftarJenisKelamin        = findViewById(R.id.RGDaftarJenisKelamin);
        RBDaftarLakiLaki            = findViewById(R.id.RBDaftarLakiLaki);
        RBDaftarPerempuan           = findViewById(R.id.RBDaftarPerempuan);

        SP                          = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        firebaseAuth                = FirebaseAuth.getInstance();
        progressDialog              = new ProgressDialog(this);
        inputMethodManager          = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        progressDialog.setMessage(getString(R.string.Tunggu_Sebentar_));
        progressDialog.setCancelable(true);

        txtJudulDaftarNoTelepon.setText(R.string.masukkan_nomor_telepon_disini);
        LLDaftarDataNoTelepon.setVisibility(View.GONE);
        LLDaftarNoTelepon.setVisibility(View.VISIBLE);
        btnKirimOTP.setVisibility(View.VISIBLE);
        LLKodeOTP.setVisibility(View.GONE);

        txtDaftarNoTelepon.addTextChangedListener(new PatternedTextWatcher("###-####-#####"));
        btnKirimOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.abuabu));
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        txtKodeOTP.addTextChangedListener(new PatternedTextWatcher("# # # # # #"));
        txtDaftarNoTelepon.requestFocus();
        btnKirimOTP.setEnabled(false);

        validasiNoTelepon();
        kirimOTP();
        balasanOTP();
        kirimUlangOTP();
        validasiKodeOTP();
        daftarNoTelepon();
        setSupportActionBar(toolbarNomorTelepon);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        PushDownAnim.setPushDownAnimTo(btnKirimOTP, txtCountdownOTP, btnDaftarNoTelepon);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.onOptionsItemSelected(item);
    }

    public void validasiNoTelepon(){
        txtDaftarNoTelepon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(txtDaftarNoTelepon.getText().length() >= 12){
                    btnKirimOTP.setBackgroundColor(ContextCompat.getColor(ActivityDaftarTelepon.this, R.color.colorPrimary));
                    btnKirimOTP.setEnabled(true);
                }
                else{
                    btnKirimOTP.setBackgroundColor(ContextCompat.getColor(ActivityDaftarTelepon.this, R.color.abuabu));
                    btnKirimOTP.setEnabled(false);
                }
                txtDaftarNoTeleponError.setVisibility(View.GONE);
            }
        });
    }

    public void kirimOTP(){
        btnKirimOTP.setOnClickListener(v -> {
            NoTelepon           = "+62" + txtDaftarNoTelepon.getText().toString().replace("-", "");
            databaseReference   = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));

            databaseReference.orderByChild("Nomor_Telepon").equalTo(NoTelepon.replace("+62", "0")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                        txtDaftarNoTeleponError.setText(getString(R.string.Nomor_Telepon_sudah_digunakan));
                    }
                    else{
                        progressDialog.show();
                        verifyPhoneNumber();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });
    }

    public void verifyPhoneNumber(){
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(ActivityDaftarTelepon.this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .setForceResendingToken(resendingToken)
                .setPhoneNumber(NoTelepon)
                .setTimeout(60L, TimeUnit.SECONDS)
                .build());
    }

    public void balasanOTP(){
        onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                OTP = phoneAuthCredential.getSmsCode();
                txtKodeOTP.setText(OTP);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) { }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verifikasiID    = s;
                resendingToken  = forceResendingToken;

                txtJudulDaftarNoTelepon.setText(R.string.Masukkan_OTP_yang_telah_dikirimkan_);
                visibilityDaftar(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                txtCountdownOTP.setEnabled(false);
                txtKodeOTP.requestFocus();
                progressDialog.dismiss();

                new CountDownTimer(60000, 1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        txtCountdownOTP.setText(String.format(Locale.getDefault(),
                                "%02d:%02d",
                                ((millisUntilFinished / (1000 * 60) % 60)), (millisUntilFinished / 1000) % 60));
                    }

                    @Override
                    public void onFinish() {
                        txtCountdownOTP.setEnabled(true);
                        txtCountdownOTP.setText(R.string.Kirim_Ulang);
                    }
                }.start();
            }
        };
    }

    public void visibilityDaftar(int DaftarNoTelepon, int KirimOTP, int KodeOTP, int DaftarDataNoTelepon){
        LLDaftarDataNoTelepon.setVisibility(DaftarDataNoTelepon);
        LLDaftarNoTelepon.setVisibility(DaftarNoTelepon);
        btnKirimOTP.setVisibility(KirimOTP);
        LLKodeOTP.setVisibility(KodeOTP);
    }

    public void kirimUlangOTP(){
        txtCountdownOTP.setOnClickListener(v -> {
            progressDialog.show();
            verifyPhoneNumber();
        });
    }

    public void validasiKodeOTP(){
        txtKodeOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(txtKodeOTP.getText().toString().replace(" ", "").equals(OTP)){
                    visibilityDaftar(View.GONE, View.GONE, View.GONE, View.VISIBLE);
                    txtJudulDaftarNoTelepon.setText(getString(R.string.Lengkapi_data_berikut_));
                    txtDaftarNamaLengkap.requestFocus();
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void daftarNoTelepon(){
        visibilityError(txtDaftarNamaLengkap);
        visibilityError(txtDaftarEmail);
        visibilityError(txtDaftarPassword);
        visibilityError(txtDaftarKonfirmasiPassword);

        btnDaftarNoTelepon.setOnClickListener(v -> {
            if(txtDaftarNamaLengkap.getText().toString().isEmpty()){
                txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                txtDaftarNoTeleponError.setText(getString(R.string.Wajib_di_isi_));
                txtDaftarNamaLengkap.requestFocus();
            }
            else if(txtDaftarEmail.getText().toString().isEmpty()){
                txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                txtDaftarNoTeleponError.setText(getString(R.string.Wajib_di_isi_));
                txtDaftarEmail.requestFocus();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(txtDaftarEmail.getText().toString()).matches()){
                txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                txtDaftarNoTeleponError.setText(getString(R.string.Email_tidak_benar_));
                txtDaftarEmail.requestFocus();
            }
            else if(txtDaftarPassword.getText().length() < 6){
                txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                txtDaftarNoTeleponError.setText(getString(R.string.Minimal_6_karakter_));
                txtDaftarPassword.requestFocus();
            }
            else if(!txtDaftarPassword.getText().toString().equals(txtDaftarKonfirmasiPassword.getText().toString())){
                txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                txtDaftarNoTeleponError.setText(getString(R.string.Password_tidak_sama_));
                txtDaftarKonfirmasiPassword.requestFocus();
            }
            else if(!RBDaftarLakiLaki.isChecked() && !RBDaftarPerempuan.isChecked()){
                RGDaftarJenisKelamin.requestFocus();
            }
            else{
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                progressDialog.setMessage(getString(R.string.Menyimpan_Data_));
                progressDialog.setCancelable(false);
                progressDialog.show();

                Map<String, Object> Pengguna = new HashMap<>();
                Pengguna.put("Jenis_Kelamin", (RBDaftarLakiLaki.isChecked()) ? "Pria" : "Wanita");
                Pengguna.put("Nomor_Telepon", NoTelepon.replace("+62", "0"));
                Pengguna.put("Password", txtDaftarKonfirmasiPassword.getText().toString());
                Pengguna.put("Nama_Lengkap", txtDaftarNamaLengkap.getText().toString());
                Pengguna.put("Email", txtDaftarEmail.getText().toString());
                Pengguna.put("Foto_Profil", "");

                if(SP.getString("MasukSebagai", "").equals("Pemilik")){
                    Pengguna.put("Verifikasi", "Tidak");
                }

                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
                databaseReference.orderByChild("Email").equalTo(txtDaftarEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                            txtDaftarNoTeleponError.setText(R.string.Email_sudah_digunakan_);
                            txtDaftarEmail.requestFocus();
                            progressDialog.dismiss();
                        }
                        else{
                            firebaseAuth.createUserWithEmailAndPassword(txtDaftarEmail.getText().toString(), txtDaftarKonfirmasiPassword.getText().toString())
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            firebaseAuth.signInWithEmailAndPassword(txtDaftarEmail.getText().toString(), txtDaftarKonfirmasiPassword.getText().toString())
                                                    .addOnCompleteListener(task1 -> {
                                                        if(task1.isSuccessful()){
                                                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Pengguna);
                                                            SP.edit().putString("KeluarAplikasi", "Ya").apply();
                                                            SP.edit().putString("MenuBawah", "Profil").apply();
                                                            SP.edit().putString("Login", "NoTelepon").apply();

                                                            startActivity(new Intent(ActivityDaftarTelepon.this, ActivityMain.class));
                                                            progressDialog.dismiss();
                                                            finish();
                                                        }
                                                        else{
                                                            txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                                                            txtDaftarNoTeleponError.setText(getString(R.string.Silahkan_coba_lagi_));
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                        }
                                        else{
                                            txtDaftarNoTeleponError.setVisibility(View.VISIBLE);
                                            txtDaftarNoTeleponError.setText(getString(R.string.Silahkan_coba_lagi_));
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }

    public void visibilityError(TextInputEditText textInputEditText){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtDaftarNoTeleponError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}