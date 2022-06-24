package com.aplikasi_ekostkarawang.Beranda.Favorite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.MyView> {
    private Context context;
    private ArrayList<ArrayFavorite> arrayFavorite;

    static class MyView extends RecyclerView.ViewHolder {
        ImageView imgFotoProfilListFavorite;
        TextView txtNamaAkunListFavorit, txtEmailAkunListFavorite;

        MyView(@NonNull View itemView) {
            super(itemView);

            imgFotoProfilListFavorite   = itemView.findViewById(R.id.imgFotoProfilListFavorite);
            txtNamaAkunListFavorit      = itemView.findViewById(R.id.txtNamaAkunListFavorite);
            txtEmailAkunListFavorite    = itemView.findViewById(R.id.txtEmailAkunListFavorite);
        }
    }

    AdapterFavorite(Context context, ArrayList<ArrayFavorite> arrayFavorite) {
        this.context        = context;
        this.arrayFavorite  = arrayFavorite;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if(!arrayFavorite.get(position).getFoto_Profil().isEmpty()){
            Glide.with(context).asBitmap().load(arrayFavorite.get(position).getFoto_Profil()).into(holder.imgFotoProfilListFavorite);
        }

        holder.txtNamaAkunListFavorit.setText(arrayFavorite.get(position).getNama_Lengkap());
        holder.txtEmailAkunListFavorite.setText(arrayFavorite.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return arrayFavorite.size();
    }
}