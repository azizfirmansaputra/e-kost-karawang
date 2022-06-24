package com.aplikasi_ekostkarawang.Profil.VerifikasiAkun;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi_ekostkarawang.Lain.ActivityMediaFullScreen;
import com.aplikasi_ekostkarawang.Lain.ArrayAkun;
import com.aplikasi_ekostkarawang.Lain.Notifikasi;
import com.aplikasi_ekostkarawang.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterVerifikasiAdmin extends RecyclerView.Adapter<AdapterVerifikasiAdmin.MyView> {
    private Activity activity;
    private ArrayList<ArrayAkun> arrayAkun;
    private String Alasan_Batalkan, Foto_KTP, Foto_Diri;

    AdapterVerifikasiAdmin(Activity activity, ArrayList<ArrayAkun> arrayAkun) {
        this.activity   = activity;
        this.arrayAkun  = arrayAkun;
    }

    static class MyView extends RecyclerView.ViewHolder {
        LinearLayout LLVerifikasiAdmin;
        ImageView imgVerifikasiFotoKTP, imgVerifikasiFotoDiri;
        Button btnVerifikasiTerima, btnVerifikasiTolak;
        TextView txtVerifikasiNamaAkun, txtVerifikasiEmailAkun;
        CircleImageView CIVVerifikasiFotoProfil;

        MyView(@NonNull View itemView) {
            super(itemView);

            LLVerifikasiAdmin       = itemView.findViewById(R.id.LLVerifikasiAdmin);
            imgVerifikasiFotoKTP    = itemView.findViewById(R.id.imgVerifikasiFotoKTP);
            imgVerifikasiFotoDiri   = itemView.findViewById(R.id.imgVerifikasiFotoDiri);
            btnVerifikasiTerima     = itemView.findViewById(R.id.btnVerifikasiTerima);
            btnVerifikasiTolak      = itemView.findViewById(R.id.btnVerifikasiTolak);
            txtVerifikasiNamaAkun   = itemView.findViewById(R.id.txtVerifikasiNamaAkun);
            txtVerifikasiEmailAkun  = itemView.findViewById(R.id.txtVerifikasiEmailAkun);
            CIVVerifikasiFotoProfil = itemView.findViewById(R.id.CIVVerifikasiFotoProfil);
        }
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_verifikasi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        cekNotifikasi(holder, position);
        mengambilDataGambar(holder, position);

        holder.txtVerifikasiNamaAkun.setText(arrayAkun.get(position).getNama_Lengkap());
        holder.txtVerifikasiEmailAkun.setText(arrayAkun.get(position).getEmail());

        if(!arrayAkun.get(position).getFoto_Profil().isEmpty()){
            Picasso.get().load(arrayAkun.get(position).getFoto_Profil()).placeholder(R.drawable.icon_profil).into(holder.CIVVerifikasiFotoProfil);
        }

        PushDownAnim.setPushDownAnimTo(holder.btnVerifikasiTerima, holder.btnVerifikasiTolak);
        verifikasiTerima(holder.btnVerifikasiTerima, position);
        verifikasiTolak(holder.btnVerifikasiTolak, position);
    }

    private void cekNotifikasi(MyView holder, int position){
        for(int i = 0; i < arrayAkun.get(position).getArrayNotifikasi().size(); i++){
            if(arrayAkun.get(position).getArrayNotifikasi().get(i).equals(arrayAkun.get(position).getID_Akun())){
                holder.LLVerifikasiAdmin.setBackgroundColor(activity.getColor(R.color.kuning_notifikasi));
            }
        }
    }

    private void mengambilDataGambar(MyView holder, int position){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Verifikasi").child(arrayAkun.get(position).getID_Akun());
        storageReference.child("Gambar_KTP.jpg").getDownloadUrl().addOnSuccessListener(uri -> storageReference.child("Gambar_Data_Diri.jpg").getDownloadUrl()
                .addOnSuccessListener(uri1 -> {
                    Picasso.get().load(uri.toString()).placeholder(R.drawable.icon_image).into(holder.imgVerifikasiFotoKTP);
                    Picasso.get().load(uri1.toString()).placeholder(R.drawable.icon_image).into(holder.imgVerifikasiFotoDiri);

                    Foto_KTP        = uri.toString();
                    Foto_Diri       = uri1.toString();
                    TypedValue TV   = new TypedValue();

                    FirebaseDatabase.getInstance().getReference().child("Notifikasi").child("Admin").child("Verifikasi").removeValue();
                    activity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, TV, true);
                    lihatGambarFull(holder.imgVerifikasiFotoKTP, Foto_KTP, activity.getString(R.string.foto_kartu_identitas));
                    lihatGambarFull(holder.imgVerifikasiFotoDiri, Foto_Diri, activity.getString(R.string.foto_diri));
                    holder.LLVerifikasiAdmin.setBackgroundResource(TV.resourceId);
                }));
    }

    private void lihatGambarFull(ImageView imgVerifikasiFoto, String Foto, String NamaFoto){
        imgVerifikasiFoto.setOnClickListener(v -> {
            imgVerifikasiFoto.setTransitionName("MediaFullScreen");

            Intent intent               = new Intent(activity, ActivityMediaFullScreen.class);
            ActivityOptionsCompat AOC   = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgVerifikasiFoto, "MediaFullScreen");

            activity.startActivity(intent.putExtra("NamaMedia", NamaFoto).putExtra("Media", Foto), AOC.toBundle());
        });
    }

    private void verifikasiTerima(Button btnVerifikasiTerima, int position){
        btnVerifikasiTerima.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik").child(arrayAkun.get(position).getID_Akun()).child("Verifikasi").setValue("Ya");
            new Notifikasi(arrayAkun.get(position).getToken(), activity.getString(R.string.Verifikasi_Akun_Baru), activity.getString(R.string.Akun_Anda_Berhasil_di_Verifikasi) + "\n" +
                    activity.getString(R.string.Sekarang_Anda_dapat_Menambahkan_Data_Kost), activity.getApplicationContext(), activity).kirimNotifikasi();
            arrayAkun.remove(position);
            notifyDataSetChanged();
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void verifikasiTolak(Button btnVerifikasiTolak, int position){
        btnVerifikasiTolak.setOnClickListener(v -> {
            BottomSheetDialog BSD = new BottomSheetDialog(activity);
            BSD.setContentView(R.layout.bottom_alasan_batalkan);
            BSD.setCanceledOnTouchOutside(true);
            BSD.show();

            TextView txtJudulPilihAlasan    = BSD.findViewById(R.id.txtJudulPilihAlasan);
            RadioGroup RGAlasanBatalkan     = BSD.findViewById(R.id.RGAlasanBatalkan);
            RadioButton RBAlasanSatu        = BSD.findViewById(R.id.RBAlasanSatu);
            RadioButton RBAlasanDua         = BSD.findViewById(R.id.RBAlasanDua);
            RadioButton RBAlasanTiga        = BSD.findViewById(R.id.RBAlasanTiga);
            RadioButton RBAlasanLainnya     = BSD.findViewById(R.id.RBAlasanLainnya);
            EditText txtAlasanLainnya       = BSD.findViewById(R.id.txtAlasanLainnya);
            Button btnKonfirmasiBatalkan    = BSD.findViewById(R.id.btnKonfirmasiBatalkan);

            txtJudulPilihAlasan.setText(R.string.Pilih_Alasan_Data_di_Tolak);
            RBAlasanSatu.setText(R.string.Foto_Buram_atau_Tidak_Jelas);
            RBAlasanDua.setText(R.string.Foto_Tidak_Terbaca);
            RBAlasanTiga.setText(R.string.Foto_Tidak_Sesuai);

            btnKonfirmasiBatalkan.setEnabled(false);
            PushDownAnim.setPushDownAnimTo(btnKonfirmasiBatalkan);
            btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(activity, R.color.abuabu));

            RGAlasanBatalkan.setOnCheckedChangeListener((group, checkedId) -> {
                txtAlasanLainnya.getText().clear();
                txtAlasanLainnya.setEnabled(false);
                btnKonfirmasiBatalkan.setEnabled(true);
                btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(activity, R.color.colorPrimary));

                if(RBAlasanSatu.isChecked()){
                    Alasan_Batalkan = "Foto Buram atau Tidak Jelas";
                }
                else if(RBAlasanDua.isChecked()){
                    Alasan_Batalkan = "Foto Tidak Terbaca";
                }
                else if(RBAlasanTiga.isChecked()){
                    Alasan_Batalkan = "Foto Tidak Sesuai";
                }
                else if(RBAlasanLainnya.isChecked()){
                    txtAlasanLainnya.setEnabled(true);
                    txtAlasanLainnya.requestFocus();
                    btnKonfirmasiBatalkan.setEnabled(false);
                    btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(activity, R.color.abuabu));
                }
            });

            txtAlasanLainnya.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(txtAlasanLainnya.getText().toString().isEmpty()){
                        btnKonfirmasiBatalkan.setEnabled(false);
                        btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(activity, R.color.abuabu));
                    }
                    else{
                        btnKonfirmasiBatalkan.setEnabled(true);
                        btnKonfirmasiBatalkan.setBackground(ContextCompat.getDrawable(activity, R.color.colorPrimary));
                    }
                    Alasan_Batalkan = txtAlasanLainnya.getText().toString();
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            btnKonfirmasiBatalkan.setOnClickListener(v1 -> {
                FirebaseDatabase.getInstance().getReference().child("Pengguna").child("Pemilik").child(arrayAkun.get(position).getID_Akun()).child("Verifikasi").setValue("Tidak");
                new Notifikasi(arrayAkun.get(position).getToken(), activity.getString(R.string.Verifikasi_Akun_Baru), activity.getString(R.string.Verifikasi_Akun_Anda_di_Tolak) + "\n" +
                        activity.getString(R.string.karena) + Alasan_Batalkan, activity.getApplicationContext(), activity).kirimNotifikasi();

                arrayAkun.remove(position);
                notifyDataSetChanged();
                BSD.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        return arrayAkun.size();
    }
}