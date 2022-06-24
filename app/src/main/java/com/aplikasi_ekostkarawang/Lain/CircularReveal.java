package com.aplikasi_ekostkarawang.Lain;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

import com.aplikasi_ekostkarawang.R;

public class CircularReveal {

    public static void circularRevealOpenActivity(Bundle savedInstanceState, View RootLayout, Activity activity){
        if(savedInstanceState == null){
            RootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver VTO = RootLayout.getViewTreeObserver();
            if(VTO.isAlive()){
                VTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity(RootLayout, activity);
                        RootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    private static void circularRevealActivity(View RootLayout, Activity activity) {
        int revealX     = RootLayout.getRight() - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, activity.getResources().getDisplayMetrics());
        int revealY     = RootLayout.getTop() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, activity.getResources().getDisplayMetrics());
        float radius    = Math.max(RootLayout.getWidth(), RootLayout.getHeight());
        Animator anim   = ViewAnimationUtils.createCircularReveal(RootLayout, revealX+30, revealY+125, 0, radius);

        anim.setDuration(500);
        RootLayout.setVisibility(View.VISIBLE);
        anim.start();
    }

    public static void circularRevealCloseActivity(View RootLayout, Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int revealX     = RootLayout.getWidth() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, activity.getResources().getDisplayMetrics());
            int revealY     = RootLayout.getTop() - (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, activity.getResources().getDisplayMetrics());
            float radius    = Math.max(RootLayout.getWidth(), RootLayout.getHeight());
            Animator anim   = ViewAnimationUtils.createCircularReveal(RootLayout, revealX+30, revealY+125, radius, 0);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
//                    Objects.requireNonNull(getSupportActionBar()).hide();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    RootLayout.setVisibility(View.INVISIBLE);
                    activity.finishAfterTransition();
                    activity.overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
            anim.setDuration(500);
            anim.start();
        }
        else{
            activity.finishAfterTransition();
        }
    }
}