package com.aplikasi_ekostkarawang.Profil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.aplikasi_ekostkarawang.ActivityMain;
import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.Profil.Akun.ActivityAkun;
import com.aplikasi_ekostkarawang.Profil.DaftarTelepon.ActivityDaftarTelepon;
import com.aplikasi_ekostkarawang.Profil.KelolaKost.ActivityKelolaKostAdmin;
import com.aplikasi_ekostkarawang.Profil.VerifikasiAkun.ActivityVerifikasi;
import com.aplikasi_ekostkarawang.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragmentProfil extends Fragment {
    private CircleImageView CIVFotoProfilAkun;
    private Button btnMasukDaftar, btnKelolaKostPemilik;
    private LinearLayout LLProfilAkun, LLEmailAdmin, LLNoHPAdmin;
    private CardView CVProfilAkun, CVMasukDaftar, CVKelolaKostPemilik;
    private TextView txtNamaLengkapAkun, txtEmailAkun, btnKeluarAkun;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences SP;

    private Dialog dialog;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view               = inflater.inflate(R.layout.fragment_profil, container, false);

        CIVFotoProfilAkun       = view.findViewById(R.id.CIVFotoProfilAkun);
        btnMasukDaftar          = view.findViewById(R.id.btnMasukDaftar);
        btnKelolaKostPemilik    = view.findViewById(R.id.btnKelolaKostPemilik);
        LLProfilAkun            = view.findViewById(R.id.LLProfilAkun);
        LLEmailAdmin            = view.findViewById(R.id.LLEmailAdmin);
        LLNoHPAdmin             = view.findViewById(R.id.LLNoHPAdmin);
        CVProfilAkun            = view.findViewById(R.id.CVProfilAkun);
        CVMasukDaftar           = view.findViewById(R.id.CVMasukDaftar);
        CVKelolaKostPemilik     = view.findViewById(R.id.CVKelolaKostPemilik);
        txtNamaLengkapAkun      = view.findViewById(R.id.txtNamaLengkapAkun);
        txtEmailAkun            = view.findViewById(R.id.txtEmailAkun);
        btnKeluarAkun           = view.findViewById(R.id.btnKeluarAkun);

        firebaseAuth            = FirebaseAuth.getInstance();
        firebaseUser            = firebaseAuth.getCurrentUser();

        googleSignInClient      = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        SP                      = requireActivity().getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);

        progressDialog  = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.Tunggu_Sebentar_));
        progressDialog.setCancelable(true);

        dialog          = new Dialog(requireContext(), R.style.CustomDialogAnimation);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cekMasuk();
        masukDaftar();
        kelolaKostPemilik();
        hubungiAdmin();
        keluarAkun();
        PushDownAnim.setPushDownAnimTo(btnMasukDaftar, btnKelolaKostPemilik, btnKeluarAkun);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void cekMasuk(){
        if(firebaseUser != null){
            if(SP.getString("Login", "Google").equals("Google")){
                if(Objects.equals(firebaseUser.getEmail(), getString(R.string.ekostkarawang_gmail_com))){
                    SP.edit().putString("MasukSebagai", "Admin").apply();
                    txtEmailAkun.setText(firebaseUser.getEmail());
                    txtNamaLengkapAkun.setText(firebaseUser.getDisplayName() + getString(R.string.Admin));
                    Picasso.get().load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.icon_image).into(CIVFotoProfilAkun);
                }
                else{
                    cekPengguna();
                }
            }
            else{
                cekPengguna();
            }

            if(SP.getString("MasukSebagai", "").equals("Pemilik") ||
                    SP.getString("MasukSebagai", "").equals("Admin")){
                CVKelolaKostPemilik.setVisibility(View.VISIBLE);
            }
            else{
                CVKelolaKostPemilik.setVisibility(View.GONE);
            }

            CVProfilAkun.setVisibility(View.VISIBLE);
            CVMasukDaftar.setVisibility(View.GONE);
            btnKeluarAkun.setVisibility(View.VISIBLE);
        }
        else{
            CVProfilAkun.setVisibility(View.GONE);
            CVMasukDaftar.setVisibility(View.VISIBLE);
            CVKelolaKostPemilik.setVisibility(View.GONE);
            btnKeluarAkun.setVisibility(View.GONE);
        }
    }

    private void cekPengguna(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
        databaseReference.orderByChild("Email").equalTo(firebaseUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(!String.valueOf(dataSnapshot.child("Foto_Profil").getValue()).isEmpty()){
                            Picasso.get().load(String.valueOf(dataSnapshot.child("Foto_Profil").getValue())).placeholder(R.drawable.icon_profil).into(CIVFotoProfilAkun);
                        }

                        txtNamaLengkapAkun.setText(String.valueOf(dataSnapshot.child("Nama_Lengkap").getValue()));
                        txtEmailAkun.setText(String.valueOf(dataSnapshot.child("Email").getValue()));
                        SP.edit().putString("IDAkun", String.valueOf(dataSnapshot.getKey())).apply();
                    }
                }
                else{
                    Map<String, Object> pengguna = new HashMap<>();
                    pengguna.put("Nama_Lengkap", firebaseUser.getDisplayName());
                    pengguna.put("Email", firebaseUser.getEmail());
                    pengguna.put("Password", "");
                    pengguna.put("Jenis_Kelamin", "");
                    pengguna.put("Nomor_Telepon", "");
                    pengguna.put("Foto_Profil", firebaseUser.getPhotoUrl().toString());

                    if(SP.getString("MasukSebagai", "").equals("Pemilik")){
                        pengguna.put("Verifikasi", "Tidak");
                    }

                    Picasso.get().load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.icon_profil).into(CIVFotoProfilAkun);
                    txtNamaLengkapAkun.setText(firebaseUser.getDisplayName());
                    txtEmailAkun.setText(firebaseUser.getEmail());

                    databaseReference.child(firebaseUser.getUid()).setValue(pengguna);
                    SP.edit().putString("IDAkun", firebaseUser.getUid()).apply();
                }

                SP.edit().putString("NamaAkun", txtNamaLengkapAkun.getText().toString()).apply();
                SP.edit().putString("EmailAkun", txtEmailAkun.getText().toString()).apply();
                ubahProfilAkun();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void ubahProfilAkun(){
        LLProfilAkun.setOnClickListener(v -> {
            if(!SP.getString("MasukSebagai", "").equals("Admin")){
                CIVFotoProfilAkun.setTransitionName("FotoProfilAkun");

                ActivityOptionsCompat AOC = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), CIVFotoProfilAkun, "FotoProfilAkun");
                Intent intent             = new Intent(requireContext(), ActivityAkun.class);

                startActivity(intent, AOC.toBundle());
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void masukDaftar(){
        btnMasukDaftar.setOnClickListener(v -> {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(R.layout.dialog_masuk_sebagai);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            Button btnPencariKost   = dialog.findViewById(R.id.btnPencariKost);
            Button btnPemilikKost   = dialog.findViewById(R.id.btnPemilikKost);

            PushDownAnim.setPushDownAnimTo(btnPencariKost, btnPemilikKost);

            btnPencariKost.setOnClickListener(v12 -> {
                dialog.dismiss();
                SP.edit().putString("MasukSebagai", "Pencari").apply();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.dialog_masuk_daftar);
                dialog.setCancelable(true);
                dialog.show();

                mendaftar();
            });

            btnPemilikKost.setOnClickListener(v1 -> {
                dialog.dismiss();
                SP.edit().putString("MasukSebagai", "Pemilik").apply();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setContentView(R.layout.dialog_masuk_daftar);
                dialog.setCancelable(true);
                dialog.show();

                mendaftar();
            });
        });
    }

    private void mendaftar(){
        final TextView txtJudulDaftar               = dialog.findViewById(R.id.txtJudulDaftar);
        final LinearLayout LLDaftarTelepon          = dialog.findViewById(R.id.LLDaftarTelepon);
        final LinearLayout LLDaftarGoogle           = dialog.findViewById(R.id.LLDaftarGoogle);
        final LinearLayout LLMasuk                  = dialog.findViewById(R.id.LLMasuk);
        final TextView txtMasukError                = dialog.findViewById(R.id.txtMasukError);
        final TextInputEditText txtEmailMasuk       = dialog.findViewById(R.id.txtEmailMasuk);
        final TextInputEditText txtPasswordMasuk    = dialog.findViewById(R.id.txtPasswordMasuk);
        final Button btnMasuk                       = dialog.findViewById(R.id.btnMasuk);
        final TextView txtPunyaAkunMasuk            = dialog.findViewById(R.id.txtPunyaAkunMasuk);
        final TextView txtMasuk                     = dialog.findViewById(R.id.txtMasuk);

        LLMasuk.setVisibility(View.GONE);
        daftar(LLDaftarTelepon, LLDaftarGoogle);
        judulDaftar(txtJudulDaftar, getString(R.string.mendaftar_pencari_kost), getString(R.string.mendaftar_pemilik_kost));
        PushDownAnim.setPushDownAnimTo(LLDaftarTelepon, LLDaftarGoogle, btnMasuk, txtMasuk);

        txtMasuk.setOnClickListener(v -> {
            if(txtMasuk.getText().toString().equals(getString(R.string.masuk))){
                judulDaftar(txtJudulDaftar, getString(R.string.Masuk_Pencari_Kost), getString(R.string.Masuk_Pemilik_Kost));
                txtPunyaAkunMasuk.setText(R.string.Belum_Punya_Akun_);
                txtMasuk.setText(R.string.Daftar);

                txtEmailMasuk.setText("");
                txtPasswordMasuk.setText("");

                YoYo.with(Techniques.FadeInDown).duration(500).playOn(LLMasuk);
                LLDaftarTelepon.setVisibility(View.GONE);
                LLDaftarGoogle.setVisibility(View.GONE);
                LLMasuk.setVisibility(View.VISIBLE);

                visibiltyError(txtEmailMasuk, txtMasukError);
                visibiltyError(txtPasswordMasuk, txtMasukError);
                masuk(txtMasukError, txtEmailMasuk, txtPasswordMasuk, btnMasuk);
            }
            else{
                judulDaftar(txtJudulDaftar, getString(R.string.mendaftar_pencari_kost), getString(R.string.mendaftar_pemilik_kost));
                txtPunyaAkunMasuk.setText(R.string.sudah_punya_akun);
                txtMasuk.setText(R.string.masuk);

                YoYo.with(Techniques.FadeInDown).duration(500).playOn(LLDaftarTelepon);
                YoYo.with(Techniques.FadeInDown).duration(500).playOn(LLDaftarGoogle);
                LLDaftarTelepon.setVisibility(View.VISIBLE);
                LLDaftarGoogle.setVisibility(View.VISIBLE);
                LLMasuk.setVisibility(View.GONE);
            }
        });
    }

    private void judulDaftar(TextView txtJudulDaftar, String Pencari, String Pemilik){
        if(SP.getString("MasukSebagai", "").equals("Pencari")){
            txtJudulDaftar.setText(Pencari);
        }
        else{
            txtJudulDaftar.setText(Pemilik);
        }
    }

    @SuppressWarnings("deprecation")
    private void daftar(LinearLayout LLDaftarTelepon, LinearLayout LLDaftarGoogle){
        LLDaftarTelepon.setOnClickListener(v -> startActivity(new Intent(getContext(), ActivityDaftarTelepon.class)));

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("717224058582-v3n0frfreko0kdh2b8e8ormpd2k71gfa.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);

        LLDaftarGoogle.setOnClickListener(v -> {
            startActivityForResult(googleSignInClient.getSignInIntent(), 2);
            progressDialog.show();
        });
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(signInAccountTask.isSuccessful()){
                try{
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);

                    if(googleSignInAccount != null){
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), task -> {
                            if(task.isSuccessful()){
                                SP.edit().putString("Login", "Google").apply();
                                SP.edit().putString("MenuBawah", "Profil").apply();
                                startActivity(new Intent(getContext(), ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                progressDialog.dismiss();
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        });
                    }
                }
                catch(ApiException e){
                    e.printStackTrace();
                }
            }
        }
        else{
            progressDialog.dismiss();
        }
    }

    private void visibiltyError(TextInputEditText textInputEditText, TextView txtMasukError){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtMasukError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void masuk(final TextView txtMasukError, final TextInputEditText txtEmailMasuk, final TextInputEditText txtPasswordMasuk, Button btnMasuk){
        btnMasuk.setOnClickListener(v -> {
            if(txtEmailMasuk.getText().toString().isEmpty()){
                txtMasukError.setVisibility(View.VISIBLE);
                txtMasukError.setText(getString(R.string.Wajib_di_isi_));
                txtEmailMasuk.requestFocus();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmailMasuk.getText().toString()).matches()){
                txtMasukError.setVisibility(View.VISIBLE);
                txtMasukError.setText(getString(R.string.Email_tidak_benar_));
                txtEmailMasuk.requestFocus();
            }
            else if(txtPasswordMasuk.getText().toString().isEmpty()){
                txtMasukError.setVisibility(View.VISIBLE);
                txtMasukError.setText(getString(R.string.Wajib_di_isi_));
                txtPasswordMasuk.requestFocus();
            }
            else{
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child(SP.getString("MasukSebagai", ""));
                databaseReference.orderByChild("Email").equalTo(txtEmailMasuk.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(String.valueOf(dataSnapshot.child("Password").getValue()).equals(txtPasswordMasuk.getText().toString())){
                                    firebaseAuth.signInWithEmailAndPassword(txtEmailMasuk.getText().toString(), txtPasswordMasuk.getText().toString())
                                            .addOnCompleteListener(task -> {
                                                if(task.isSuccessful()){
                                                    SP.edit().putString("Login", "Google").apply();
                                                    SP.edit().putString("MenuBawah", "Profil").apply();

                                                    startActivity(new Intent(getContext(), ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    getActivity().finish();
                                                    dialog.dismiss();
                                                }
                                                else{
                                                    txtMasukError.setVisibility(View.VISIBLE);
                                                    txtMasukError.setText(R.string.Silahkan_coba_lagi_);
                                                }
                                            });
                                }
                                else{
                                    txtMasukError.setVisibility(View.VISIBLE);
                                    txtMasukError.setText(R.string.Password_salah_);
                                    txtPasswordMasuk.requestFocus();
                                }
                            }
                        }
                        else{
                            txtMasukError.setVisibility(View.VISIBLE);
                            txtMasukError.setText(R.string.Email_tidak_terdaftar_);
                            txtEmailMasuk.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }

    private void kelolaKostPemilik(){
        btnKelolaKostPemilik.setOnClickListener(v -> {
            if(SP.getString("MasukSebagai", "").equals("Pemilik")){
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik");
                databaseReference.orderByChild("Email").equalTo(txtEmailAkun.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(String.valueOf(dataSnapshot.child("Verifikasi").getValue()).equals("Tidak")){
                                belumVerifikasi();
                            }
                            else if(String.valueOf(dataSnapshot.child("Verifikasi").getValue()).equals("Menunggu")){
                                menungguVerifikasi();
                            }
                            else{
                                startActivity(new Intent(getContext(), ActivitySelengkapnya.class).putExtra("IDKost", SP.getString("IDAkun", "")));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            else{
                startActivity(new Intent(getContext(), ActivityKelolaKostAdmin.class));
            }
        });
    }

    private void belumVerifikasi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setIcon(R.drawable.icon_verifikasi)
                .setTitle(R.string.Verifikasi_Akun)
                .setMessage(getString(R.string.Akun_Anda_belum_di_verifikasi) + "\n" + getString(R.string.Verifikasi_Sekarang))
                .setNegativeButton(R.string.TIDAK, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.YA, (dialog, which) -> {
                    startActivity(new Intent(getContext(), ActivityVerifikasi.class));
                    dialog.dismiss();
                });
        builder.setCancelable(true);
        builder.show();
    }

    private void menungguVerifikasi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setIcon(R.drawable.icon_verifikasi)
                .setTitle(R.string.Verifikasi_Akun)
                .setMessage(getString(R.string.Akun_Anda_Sedang_di_Verifikasi) + "\n" + getString(R.string.Mohon_untuk_Menunggu))
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.setCancelable(true);
        builder.show();
    }

    private void hubungiAdmin(){
        LLEmailAdmin.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.ekostkarawang_gmail_com), null));
            email.putExtra(Intent.EXTRA_SUBJECT, "");
            email.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(email, getString(R.string.Buka_dengan_)));
        });

        LLNoHPAdmin.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + "+6285887762973"))));
    }

    @SuppressWarnings("ConstantConditions")
    private void keluarAkun(){
        btnKeluarAkun.setOnClickListener(v -> googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                dialog.setContentView(R.layout.dialog_keluar_akun);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                Button btnBatalKeluarAkun   = dialog.findViewById(R.id.btnBatalKeluarAkun);
                Button btnYaKeluarAkun      = dialog.findViewById(R.id.btnYaKeluarAkun);

                PushDownAnim.setPushDownAnimTo(btnBatalKeluarAkun, btnYaKeluarAkun);

                btnBatalKeluarAkun.setOnClickListener(v12 -> dialog.dismiss());

                btnYaKeluarAkun.setOnClickListener(v1 -> {
                    firebaseAuth.signOut();

                    SP.edit().putString("MenuBawah", "Profil").apply();
                    SP.edit().putString("EmailAkun", "").apply();
                    SP.edit().putString("NamaAkun", "").apply();
                    SP.edit().putString("IDAkun", "").apply();
                    SP.edit().putString("Login", "").apply();
                    startActivity(new Intent(getContext(), ActivityMain.class));
                    getActivity().finish();
                    dialog.dismiss();
                });
            }
        }));
    }
}