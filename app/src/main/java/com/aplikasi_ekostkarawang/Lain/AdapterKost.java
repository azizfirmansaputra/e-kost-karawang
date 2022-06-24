package com.aplikasi_ekostkarawang.Lain;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Beranda.AdapterMediaKost;
import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.R;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterKost extends RecyclerView.Adapter<AdapterKost.MyView> {
    private Context context;
    private ArrayList<ArrayKost> arrayKost;

    static class MyView extends RecyclerView.ViewHolder {
        LinearLayout LLListKost;
        TextView txtListNamaKost, txtListJarakKost, txtListAlamatKost;
        RecyclerView RVListMediaKost;
        Button btnSelengkapnya;

        MyView(@NonNull View itemView) {
            super(itemView);

            LLListKost          = itemView.findViewById(R.id.LLListKost);
            txtListNamaKost     = itemView.findViewById(R.id.txtListNamaKost);
            txtListJarakKost    = itemView.findViewById(R.id.txtListJarakKost);
            txtListAlamatKost   = itemView.findViewById(R.id.txtListAlamatKost);
            RVListMediaKost     = itemView.findViewById(R.id.RVListMediaKost);
            btnSelengkapnya     = itemView.findViewById(R.id.btnSelengkapnya);
        }
    }

    public AdapterKost(Context context, ArrayList<ArrayKost> arrayDataKost){
        this.context    = context;
        this.arrayKost  = arrayDataKost;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kost, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, final int position) {
        holder.txtListNamaKost.setText(arrayKost.get(position).getNama_Kost());
        holder.txtListAlamatKost.setText(arrayKost.get(position).getAlamat_Kost());

        holder.btnSelengkapnya.setVisibility(View.GONE);
        holder.LLListKost.setOnClickListener(v -> context.startActivity(new Intent(context, ActivitySelengkapnya.class).putExtra("Posisi", position)));

        LinearLayoutManager LLM = new LinearLayoutManager(context);
        ArrayList<String> Media = new ArrayList<>();

        Media.addAll(arrayKost.get(position).getFoto_Kost());
        Media.addAll(arrayKost.get(position).getVideo_Kost());
        Collections.shuffle(Media);

        LLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.RVListMediaKost.setLayoutManager(LLM);
        holder.RVListMediaKost.setItemAnimator(new DefaultItemAnimator());
        holder.RVListMediaKost.setAdapter(new AdapterMediaKost(context, Media));
        holder.RVListMediaKost.addItemDecoration(new DividerItemDecoration(holder.RVListMediaKost.getContext(), LLM.getOrientation()));
    }

    @Override
    public int getItemCount() {
        return arrayKost.size();
    }
}