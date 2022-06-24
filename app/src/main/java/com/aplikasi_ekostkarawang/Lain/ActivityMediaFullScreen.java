package com.aplikasi_ekostkarawang.Lain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.aplikasi_ekostkarawang.R;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.Objects;

public class ActivityMediaFullScreen extends AppCompatActivity {
    Toolbar toolbarMediaFullScreen;
    TextView txtJudulMediaFullScreen;
    ZoomInImageView ZIIVMediaFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_full_screen);

        toolbarMediaFullScreen  = findViewById(R.id.toolbarMediaFullScreen);
        txtJudulMediaFullScreen = findViewById(R.id.txtJudulMediaFullScreen);
        ZIIVMediaFullScreen     = findViewById(R.id.ZIIVMediaFullScreen);

        setSupportActionBar(toolbarMediaFullScreen);
        txtJudulMediaFullScreen.setText(getIntent().getExtras().getString("NamaMedia"));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Picasso.get().load(getIntent().getExtras().getString("Media")).placeholder(R.drawable.icon_image).into(ZIIVMediaFullScreen);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }
}