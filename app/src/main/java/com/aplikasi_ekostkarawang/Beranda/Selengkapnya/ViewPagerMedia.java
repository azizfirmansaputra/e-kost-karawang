package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerMedia extends PagerAdapter {
    private Context context;
    private ArrayList<String> Media;

    ViewPagerMedia(Context context, ArrayList<String> Media){
        this.context    = context;
        this.Media      = Media;
    }

    @Override
    public int getCount() {
        return Media.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("ConstantConditions")
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater       = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view                     = layoutInflater.inflate(R.layout.view_pager_media, container, false);

        final RelativeLayout RLMediaKost    = view.findViewById(R.id.RLMediaKost);
        final ImageView imgMediaKost        = view.findViewById(R.id.imgMediaKost);
        final WebView WVMediaKost           = view.findViewById(R.id.WVMediaKost);

        final SharedPreferences SP          = context.getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);
        Fade fade                           = new Fade();
        View dekor                          = ((Activity)context).getWindow().getDecorView();

        fade.excludeTarget(dekor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);

        ((Activity)context).getWindow().setEnterTransition(fade);
        ((Activity)context).getWindow().setExitTransition(fade);

        RLMediaKost.setTransitionName("MediaKost");
        Glide.with(context).asBitmap().load(Media.get(position)).into(imgMediaKost);

        if(Media.get(position).contains("Foto_Kost") && Media.get(position).contains(".jpg")){
            imgMediaKost.setVisibility(View.VISIBLE);
            WVMediaKost.setVisibility(View.GONE);
        }
        else{
            WVMediaKost.getSettings().setAppCacheEnabled(false);
            WVMediaKost.getSettings().setJavaScriptEnabled(true);
            WVMediaKost.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            WVMediaKost.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    imgMediaKost.setVisibility(View.VISIBLE);
                    WVMediaKost.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    imgMediaKost.setVisibility(View.GONE);
                    WVMediaKost.setVisibility(View.VISIBLE);
                }
            });

            String MediaKost =  "<html>" +
                                    "<head>" +
                                        "<style>" +
                                            "body{background-color: transparent; background-size: cover; background-repeat: no-repeat; overflow: hidden;}" +
                                            "div video{position: absolute; object-fit: fill; top: 0; bottom: 0; left: 0; right: 0; height: 100%; width: 100%;}" +
                                        "</style>" +
                                    "</head>" +
                                    "<body>" +
                                        "<div>" +
                                            "<video src=\"" + Media.get(position) + "\" poster=\"\" autoplay muted loop></video>" +
                                        "</div>" +
                                    "</body>" +
                                "</html>";

            WVMediaKost.loadDataWithBaseURL(null, MediaKost, "text/html", "UTF-8", null);
        }

        RLMediaKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SP.getString("FullScreen", "Tidak").equals("Tidak")){
                    Intent intent               = new Intent(context, ActivityMedia.class);
                    ActivityOptionsCompat AOC   = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, RLMediaKost, "MediaKost");

                    SP.edit().putString("FullScreen", "Ya").apply();
                    context.startActivity(intent.putExtra("PosisiMedia", position).putExtra("Media", Media), AOC.toBundle());
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}