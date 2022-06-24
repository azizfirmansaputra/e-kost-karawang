package com.aplikasi_ekostkarawang.Pesan;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesan extends RecyclerView.Adapter<AdapterPesan.MyView> {
    public static final int MSG_TYPE_LEFT   = 0;
    public static final int MSG_TYPE_RIGHT  = 1;

    private Context context;
    private ArrayList<ArrayPesan> arrayPesan;
    private String imgURL;

    private FirebaseUser firebaseUser;

    public AdapterPesan(Context context, ArrayList<ArrayPesan> arrayPesan, String imgURL){
        this.context    = context;
        this.arrayPesan = arrayPesan;
        this.imgURL     = imgURL;
    }

    public static class MyView extends RecyclerView.ViewHolder {
        CircleImageView CIVFotoPengirim;
        TextView txtTampilPesan, txtWaktuPesan;
        ImageView imgCeklisPesan;

        public MyView(@NonNull View itemView) {
            super(itemView);

            CIVFotoPengirim = itemView.findViewById(R.id.CIVFotoPengirim);
            txtTampilPesan  = itemView.findViewById(R.id.txtTampilPesan);
            txtWaktuPesan   = itemView.findViewById(R.id.txtWaktuPesan);
            imgCeklisPesan  = itemView.findViewById(R.id.imgCeklisPesan);
        }
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            return new MyView(LayoutInflater.from(context).inflate(R.layout.list_pesan_pengirim, parent, false));
        }
        else{
            return new MyView(LayoutInflater.from(context).inflate(R.layout.list_pesan_penerima, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.txtTampilPesan.setText(arrayPesan.get(position).getPesan());
        holder.txtWaktuPesan.setText(arrayPesan.get(position).getWaktu());

        if(!imgURL.equals("")){
            Glide.with(context).asBitmap().load(imgURL).into(holder.CIVFotoPengirim);
        }

        if(position == arrayPesan.size() - 1){
            if(arrayPesan.get(position).isTerkirim()){
                holder.imgCeklisPesan.setImageDrawable(context.getDrawable(R.drawable.icon_ceklis_dua));
                holder.imgCeklisPesan.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorPrimary)));
            }
            else{
                holder.imgCeklisPesan.setImageDrawable(context.getDrawable(R.drawable.icon_ceklis_dua));
                holder.imgCeklisPesan.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.abuabu)));
            }
        }
        else{
            if(arrayPesan.get(position).isTerkirim()){
                holder.imgCeklisPesan.setImageDrawable(context.getDrawable(R.drawable.icon_ceklis_dua));
                holder.imgCeklisPesan.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorPrimary)));
            }
            else{
                holder.imgCeklisPesan.setImageDrawable(context.getDrawable(R.drawable.icon_ceklis_dua));
                holder.imgCeklisPesan.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.abuabu)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayPesan.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(arrayPesan.get(position).getPengirim().equals(firebaseUser.getEmail())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}