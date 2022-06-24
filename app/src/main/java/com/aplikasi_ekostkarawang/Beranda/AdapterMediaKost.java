package com.aplikasi_ekostkarawang.Beranda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class AdapterMediaKost extends RecyclerView.Adapter<AdapterMediaKost.MyView> {
    private Context context;
    private ArrayList<String> MediaKost;

    static class MyView extends RecyclerView.ViewHolder {
        ImageView imgListKost;
        LinearLayout LLListVideoKost;

        MyView(@NonNull View itemView) {
            super(itemView);

            imgListKost     = itemView.findViewById(R.id.imgListKost);
            LLListVideoKost = itemView.findViewById(R.id.LLListVideoKost);
        }
    }

    public AdapterMediaKost(Context context, ArrayList<String> MediaKost){
        this.context    = context;
        this.MediaKost  = MediaKost;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_media, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if(MediaKost.get(position).contains("Foto_Kost") && MediaKost.get(position).contains(".jpg")){
            holder.LLListVideoKost.setVisibility(View.GONE);
        }
        else{
            holder.LLListVideoKost.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .asBitmap()
                .apply(new RequestOptions().centerCrop().override(125, 125))
                .load(MediaKost.get(position)).into(holder.imgListKost);
    }

    @Override
    public int getItemCount() {
        return MediaKost.size();
    }
}