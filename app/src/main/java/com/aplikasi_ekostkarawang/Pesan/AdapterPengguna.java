package com.aplikasi_ekostkarawang.Pesan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Beranda.Selengkapnya.ActivitySelengkapnya;
import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class AdapterPengguna extends RecyclerView.Adapter<AdapterPengguna.MyView> {
    private Context context;
    private ArrayList<ArrayPengguna> arrayPengguna;
    private boolean isChat;
    private String pesanAkhir;
    private String waktuPesan;

    public AdapterPengguna(Context context, ArrayList<ArrayPengguna> arrayPengguna, boolean isChat) {
        this.context        = context;
        this.arrayPengguna  = arrayPengguna;
        this.isChat         = isChat;
    }

    public class MyView extends RecyclerView.ViewHolder {
        RelativeLayout RLListPengguna;
        CircleImageView CIVListFotoPengguna;
        TextView txtListNamaPengguna, txtPesanTerakhir, txtListWaktuPesan;

        public MyView(@NonNull View itemView) {
            super(itemView);

            RLListPengguna      = itemView.findViewById(R.id.RLListPengguna);
            CIVListFotoPengguna = itemView.findViewById(R.id.CIVListFotoPengguna);
            txtListNamaPengguna = itemView.findViewById(R.id.txtListNamaPengguna);
            txtPesanTerakhir    = itemView.findViewById(R.id.txtPesanTerakhir);
            txtListWaktuPesan   = itemView.findViewById(R.id.txtListWaktuPesan);
        }
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(context).inflate(R.layout.list_pengguna, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, final int position) {
        holder.txtListNamaPengguna.setText(arrayPengguna.get(position).getNama_Lengkap());

        if(!arrayPengguna.get(position).getFoto_Profil().equals("")){
            Glide.with(context).asBitmap().load(arrayPengguna.get(position).getFoto_Profil()).into(holder.CIVListFotoPengguna);
        }

        holder.RLListPengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityPesan.class)
                        .putExtra("Posisi", position)
                        .putExtra("FotoPemilik", arrayPengguna.get(position).getFoto_Profil())
                        .putExtra("NamaPemilik", arrayPengguna.get(position).getNama_Lengkap()));
            }
        });

        if(isChat){
            pesanTerakhir(arrayPengguna.get(position).getEmail(), holder.txtPesanTerakhir, holder.txtListWaktuPesan);
        }
        else{
            holder.txtPesanTerakhir.setVisibility(View.GONE);
            holder.txtListWaktuPesan.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayPengguna.size();
    }

    public void pesanTerakhir(final String Email, final TextView pesanTerakhir, final TextView txtListWaktuPesan){
        pesanAkhir                      = "default";
        waktuPesan                      = "00:00";

        int SECOND_MILLIS  = 1000;
        int MINUTE_MILLIS  = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS     = 24 * HOUR_MILLIS;

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference     = FirebaseDatabase.getInstance().getReference().child("Pesan");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ArrayPesan Pesan = dataSnapshot.getValue(ArrayPesan.class);
                    if(Pesan.getPenerima().equals(firebaseUser.getEmail()) && Pesan.getPengirim().equals(Email) ||
                            Pesan.getPenerima().equals(Email) && Pesan.getPengirim().equals(firebaseUser.getEmail())){
                        pesanAkhir  = Pesan.getPesan();

                        if((System.currentTimeMillis() - Pesan.getTimeStamp()) > 24 * HOUR_MILLIS && (System.currentTimeMillis() - Pesan.getTimeStamp()) < 48 * HOUR_MILLIS){
                            waktuPesan = "Kemarin";
                        }
                        else if((System.currentTimeMillis() - Pesan.getTimeStamp()) > 48 * HOUR_MILLIS){
                            waktuPesan = Pesan.getTanggal();
                        }
                        else{
                            waktuPesan = Pesan.getWaktu();
                        }
                    }
                }

                if("default".equals(pesanAkhir)){
                    pesanTerakhir.setText(R.string.tidak_ada_pesan);
                    txtListWaktuPesan.setText("");
                }
                else{
                    pesanTerakhir.setText(pesanAkhir);
                    txtListWaktuPesan.setText(waktuPesan);
                }

                pesanAkhir = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}