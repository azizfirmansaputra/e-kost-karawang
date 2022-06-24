package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

class AdapterFotoVideo extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Uri> arrayListFotoVideo;
    private String jenis;

    AdapterFotoVideo(Context context, ArrayList<Uri> arrayListFotoVideo, String jenis){
        this.context            = context;
        this.arrayListFotoVideo = arrayListFotoVideo;
        this.jenis              = jenis;
    }

    @Override
    public int getCount() {
        return arrayListFotoVideo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null && inflater != null){
            convertView = inflater.inflate(R.layout.list_foto_video, null);
        }

        ImageView imgTambahFotoVideoKost    = convertView.findViewById(R.id.imgTambahFotoVideoKost);
        Button btnHapusFotoVideoKost        = convertView.findViewById(R.id.btnHapusFotoVideoKost);

        PushDownAnim.setPushDownAnimTo(btnHapusFotoVideoKost);

        if(arrayListFotoVideo.get(position) == null){
            if(jenis.equals("Foto")){
                imgTambahFotoVideoKost.setImageDrawable(context.getDrawable(R.drawable.icon_image));
            }
            else{
                imgTambahFotoVideoKost.setImageDrawable(context.getDrawable(R.drawable.icon_video));
            }
            btnHapusFotoVideoKost.setVisibility(View.GONE);
        }
        else{
            Glide.with(context).load(arrayListFotoVideo.get(position)).into(imgTambahFotoVideoKost);
            btnHapusFotoVideoKost.setVisibility(View.VISIBLE);
        }

        btnHapusFotoVideoKost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListFotoVideo.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}