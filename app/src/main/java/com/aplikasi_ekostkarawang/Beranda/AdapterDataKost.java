package com.aplikasi_ekostkarawang.Beranda;

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

import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.R;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterDataKost extends RecyclerView.Adapter<AdapterDataKost.MyView> {
    private Context context;
    public static ArrayList<ArrayDataKost> arrayDataKost;

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

    public AdapterDataKost(Context context, ArrayList<ArrayDataKost> arrayDataKost){
        this.context                    = context;
        AdapterDataKost.arrayDataKost   = arrayDataKost;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kost, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, final int position) {
        holder.txtListNamaKost.setText(arrayDataKost.get(position).getNama_Kost());
        holder.txtListAlamatKost.setText(arrayDataKost.get(position).getAlamat());

        if(arrayDataKost.get(position).getJarak().equals("null")){
            holder.txtListJarakKost.setVisibility(View.GONE);
            holder.btnSelengkapnya.setVisibility(View.GONE);
            holder.LLListKost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ActivitySelengkapnya.class).putExtra("Posisi", position));
                }
            });
        }
        else{
            holder.txtListJarakKost.setText(arrayDataKost.get(position).getJarak());
            holder.btnSelengkapnya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ActivitySelengkapnya.class).putExtra("Posisi", position));
                }
            });
        }

        LinearLayoutManager LLM = new LinearLayoutManager(context);
        ArrayList<String> Media = new ArrayList<>();

        Media.addAll(arrayDataKost.get(position).getFoto_Kost());
        Media.addAll(arrayDataKost.get(position).getVideo_Kost());
        Collections.shuffle(Media);

        LLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.RVListMediaKost.setLayoutManager(LLM);
        holder.RVListMediaKost.setItemAnimator(new DefaultItemAnimator());
        holder.RVListMediaKost.setAdapter(new AdapterMediaKost(context, Media));
        holder.RVListMediaKost.addItemDecoration(new DividerItemDecoration(holder.RVListMediaKost.getContext(), LLM.getOrientation()));
    }

    @Override
    public int getItemCount() {
        return arrayDataKost.size();
    }
}