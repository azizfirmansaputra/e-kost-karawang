package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class AdapterUlasan extends RecyclerView.Adapter<AdapterUlasan.MyView> {
    private Context context;
    private ArrayList<ArrayUlasan> arrayUlasan;

    static class MyView extends RecyclerView.ViewHolder {
        ImageView imgFotoProfilUlasan;
        TextView txtNamaAkunUlasan, txtIsiUlasan, txtTanggalUlasan;
        Button btnHapusUlasan;
        RatingBar ratingBarUlasan;

        MyView(@NonNull View itemView) {
            super(itemView);

            imgFotoProfilUlasan = itemView.findViewById(R.id.imgFotoProfilUlasan);
            txtNamaAkunUlasan   = itemView.findViewById(R.id.txtNamaAkunUlasan);
            txtIsiUlasan        = itemView.findViewById(R.id.txtIsiUlasan);
            txtTanggalUlasan    = itemView.findViewById(R.id.txtTanggalUlasan);
            btnHapusUlasan      = itemView.findViewById(R.id.btnHapusUlasan);
            ratingBarUlasan     = itemView.findViewById(R.id.ratingBarUlasan);
        }
    }

    AdapterUlasan(Context context, ArrayList<ArrayUlasan> arrayUlasan) {
        this.context        = context;
        this.arrayUlasan    = arrayUlasan;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ulasan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        final DatabaseReference DB    = FirebaseDatabase.getInstance().getReference();
        final SharedPreferences SP    = context.getSharedPreferences("E-KOST_KARAWANG", Context.MODE_PRIVATE);

        if(!SP.getString("EmailAkun", "").equals(arrayUlasan.get(position).getNama_Akun())){
            holder.btnHapusUlasan.setVisibility(View.GONE);
        }

        DB.child("Pengguna").child("Pencari").orderByChild("Email").equalTo(arrayUlasan.get(position).getNama_Akun()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!String.valueOf(dataSnapshot.child("Foto_Profil").getValue()).equals("")){
                        Glide.with(context).asBitmap().load(String.valueOf(dataSnapshot.child("Foto_Profil").getValue())).into(holder.imgFotoProfilUlasan);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.btnHapusUlasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB.child("Ulasan").child(SP.getString("ID_KOST", "")).orderByChild("Nama_Akun")
                        .equalTo(arrayUlasan.get(position).getNama_Akun()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                            arrayUlasan.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

        holder.txtNamaAkunUlasan.setText(arrayUlasan.get(position).getNama_Akun());
        holder.txtIsiUlasan.setText(arrayUlasan.get(position).getIsi_Ulasan());
        holder.ratingBarUlasan.setRating(Float.parseFloat(arrayUlasan.get(position).getRating()));
        holder.txtTanggalUlasan.setText(arrayUlasan.get(position).getTanggal());
    }

    @Override
    public int getItemCount() {
        return arrayUlasan.size();
    }
}