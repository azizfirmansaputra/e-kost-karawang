package com.aplikasi_ekostkarawang.Beranda;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.aplikasi_ekostkarawang.R;

import java.util.Objects;

public class ActivityCariLokasi extends AppCompatActivity {
    AutoCompleteTextView txtCariLokasiKost;
    Button btnKostDekatLokasiSaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        setContentView(R.layout.activity_cari_lokasi);

        txtCariLokasiKost       = findViewById(R.id.txtCariLokasiKost);
        btnKostDekatLokasiSaya  = findViewById(R.id.btnKostDekatLokasiSaya);

        Fade fade               = new Fade();
        View dekor              = getWindow().getDecorView();

        fade.excludeTarget(dekor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        cariLokasi();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void cariLokasi(){
        txtCariLokasiKost.requestFocus();
        txtCariLokasiKost.setText("mkmkm");
        InputMethodManager IMM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        IMM.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}