package com.aplikasi_ekostkarawang.Pemesanan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

class AdapterPemesanan extends RecyclerView.Adapter<AdapterPemesanan.MyView> {
    private Context context;
    static ArrayList<ArrayPemesanan> arrayPemesanan;

    static class MyView extends RecyclerView.ViewHolder {
        LinearLayout LLListPemesanan;
        CardView CVListPemesanan;
        ImageView imgListPemesanan;
        TextView txtListNomorPemesanan, txtListTanggalMasukPemesanan, txtListNamaKostPemesanan, txtListAlamatKostPemesanan, txtListStatusPembayaran, txtListHargaKostPemesanan;

        MyView(@NonNull View itemView) {
            super(itemView);

            LLListPemesanan                 = itemView.findViewById(R.id.LLListPemesanan);
            CVListPemesanan                 = itemView.findViewById(R.id.CVListPemesanan);
            imgListPemesanan                = itemView.findViewById(R.id.imgListPemesanan);
            txtListNomorPemesanan           = itemView.findViewById(R.id.txtListNomorPemesanan);
            txtListTanggalMasukPemesanan    = itemView.findViewById(R.id.txtListTanggalMasukPemesanan);
            txtListNamaKostPemesanan        = itemView.findViewById(R.id.txtListNamaKostPemesanan);
            txtListAlamatKostPemesanan      = itemView.findViewById(R.id.txtListAlamatKostPemesanan);
            txtListStatusPembayaran         = itemView.findViewById(R.id.txtListStatusPembayaran);
            txtListHargaKostPemesanan       = itemView.findViewById(R.id.txtListHargaKostPemesanan);
        }
    }

    AdapterPemesanan(Context context, ArrayList<ArrayPemesanan> arrayPemesanan) {
        this.context                    = context;
        AdapterPemesanan.arrayPemesanan = arrayPemesanan;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pemesanan, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull MyView holder, final int position) {
        SharedPreferences SP = context.getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);

        holder.txtListNomorPemesanan.setText(arrayPemesanan.get(position).getNomor_Pemesanan().toUpperCase());
        holder.txtListTanggalMasukPemesanan.setText(arrayPemesanan.get(position).getTanggal_Masuk());
        holder.txtListNamaKostPemesanan.setText(arrayPemesanan.get(position).getNama_Kost());
        holder.txtListAlamatKostPemesanan.setText(arrayPemesanan.get(position).getAlamat_Kost());
        holder.txtListStatusPembayaran.setText(Html.fromHtml("&#9679; ") + arrayPemesanan.get(position).getStatus_Pemesanan());
        holder.txtListHargaKostPemesanan.setText(arrayPemesanan.get(position).getHarga_Kost());

        if(arrayPemesanan.get(position).getStatus_Pemesanan().equals("Pesananan di Batalkan")){
            holder.txtListStatusPembayaran.setTextColor(context.getColor(android.R.color.holo_red_dark));
        }

        if(SP.getString("MasukSebagai", "").equals("Pencari")){
            Collections.shuffle(arrayPemesanan.get(position).getArrayFotoKost());
            Glide.with(context).asBitmap().load(arrayPemesanan.get(position).getArrayFotoKost().get(0)).into(holder.imgListPemesanan);

            holder.CVListPemesanan.setRadius(0);
            holder.LLListPemesanan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ActivityPemesanan.class).putExtra("PosisiPemesanan", position));
                }
            });
        }
        else if(SP.getString("MasukSebagai", "").equals("Pemilik")){
            holder.CVListPemesanan.setRadius(250);
            holder.LLListPemesanan.setBackground(null);

            if(!arrayPemesanan.get(position).getArrayFotoKost().get(0).equals("")){
                Glide.with(context).asBitmap().load(arrayPemesanan.get(position).getArrayFotoKost().get(0)).into(holder.imgListPemesanan);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayPemesanan.size();
    }
}