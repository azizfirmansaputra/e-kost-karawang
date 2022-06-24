package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.transition.Fade;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.aplikasi_ekostkarawang.R;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityMedia extends AppCompatActivity {
    ViewPager VPMediaKostFull;
    LinearLayout LLDotIndicator;

    SharedPreferences SP;
    ViewPagerMedia viewPagerMedia;

    int Posisi;
    TextView[] txtDots;
    ArrayList<String> Media = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        VPMediaKostFull = findViewById(R.id.VPMediaKostFull);
        LLDotIndicator  = findViewById(R.id.LLDotIndicator);

        Posisi          = Objects.requireNonNull(getIntent().getExtras()).getInt("PosisiMedia");
        Media           = (ArrayList<String>)getIntent().getSerializableExtra("Media");
        SP              = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        viewPagerMedia  = new ViewPagerMedia(ActivityMedia.this, Media);

        Fade fade       = new Fade();
        View dekor      = getWindow().getDecorView();

        fade.excludeTarget(dekor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        VPMediaKostFull.setAdapter(viewPagerMedia);
        VPMediaKostFull.setCurrentItem(Posisi);

        mengaturMediaKost();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void mengaturMediaKost(){
        setDot(Posisi);
        VPMediaKostFull.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        txtDots = new TextView[Media.size()];
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

    @Override
    public void onBackPressed() {
        SP.edit().putString("FullScreen", "Tidak").apply();
        VPMediaKostFull.setCurrentItem(Posisi);
        super.onBackPressed();
    }
}