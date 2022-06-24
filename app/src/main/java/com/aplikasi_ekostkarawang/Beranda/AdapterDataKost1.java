package com.aplikasi_ekostkarawang.Beranda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.R;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterDataKost1 extends ArrayAdapter<ArrayDataKost> {
    public static ArrayList<ArrayDataKost> arrayDataKost;
    SharedPreferences SP;

    AdapterDataKost1(@NonNull Context context, ArrayList<ArrayDataKost> arrayDataKost) {
        super(context, R.layout.list_kost, arrayDataKost);

        this.arrayDataKost = arrayDataKost;
    }

    @NonNull
    @Override
    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view               = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kost, null, true);

        TextView txtNamaKost    = view.findViewById(R.id.txtListNamaKost);
        TextView txtJarakKost   = view.findViewById(R.id.txtListJarakKost);
        TextView txtAlamatKost  = view.findViewById(R.id.txtListAlamatKost);
        final RecyclerView RVKost     = view.findViewById(R.id.RVListMediaKost);
        Button btnSelengkapnya  = view.findViewById(R.id.btnSelengkapnya);

        txtNamaKost.setText(arrayDataKost.get(position).getNama_Kost());
        txtJarakKost.setText(arrayDataKost.get(position).getJarak());
        txtAlamatKost.setText(arrayDataKost.get(position).getAlamat());
        PushDownAnim.setPushDownAnimTo(btnSelengkapnya);

        LinearLayoutManager LLM = new LinearLayoutManager(getContext());
        ArrayList<String> Media = new ArrayList<>();
        SP                      = getContext().getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);

        Media.addAll(arrayDataKost.get(position).getFoto_Kost());
        Media.addAll(arrayDataKost.get(position).getVideo_Kost());
        Collections.shuffle(Media);

        LLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        RVKost.setLayoutManager(LLM);
        RVKost.setItemAnimator(new DefaultItemAnimator());
        RVKost.setAdapter(new AdapterMediaKost(getContext(), Media));

//        final Fade fade   = new Fade();
//        View dekor  = ((Activity)getContext()).getWindow().getDecorView();
//
//        fade.excludeTarget(dekor.findViewById(R.id.action_bar_container), true);
//        fade.excludeTarget(android.R.id.statusBarBackground, true);
//        fade.excludeTarget(android.R.id.navigationBarBackground, true);
//
//        ((Activity)getContext()).getWindow().setEnterTransition(fade);
//        ((Activity)getContext()).getWindow().setExitTransition(fade);

        btnSelengkapnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SP.edit().putString("FullScreen", "Tidak").apply();
                Intent intent               = new Intent(getContext(), ActivitySelengkapnya.class);
//                ActivityOptionsCompat AOP   = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)getContext()), RVKost, "fade");
//                getContext().startActivity(intent.putExtra("Posisi", position), AOP.toBundle());
                getContext().startActivity(intent.putExtra("Posisi", position));
            }
        });

        return view;
    }
}