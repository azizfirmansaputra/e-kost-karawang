package com.aplikasi_ekostkarawang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(ActivitySplashScreen.this, ActivityMain.class));

            finishAfterTransition();
        }, 1000);
    }

    @Override
    public void onBackPressed() { }
}