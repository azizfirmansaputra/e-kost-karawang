package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aplikasi_ekostkarawang.R;

import java.util.ArrayList;

class AdapterFasilitasBersama extends BaseAdapter {
    private Context context;
    private ArrayList<String> fasilitas_bersama;
    private LayoutInflater inflater;

    AdapterFasilitasBersama(Context context, ArrayList<String> fasilitas_bersama){
        this.context            = context;
        this.fasilitas_bersama  = fasilitas_bersama;
    }

    @Override
    public int getCount() {
        return fasilitas_bersama.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null && inflater != null){
            convertView                         = inflater.inflate(R.layout.list_fasilitas_bersama, null);

            ImageView imgIconFasilitasBersama   = convertView.findViewById(R.id.imgIconFasilitasBersama);
            TextView txtFasilitasBersama        = convertView.findViewById(R.id.txtFasilitasBersama);

            txtFasilitasBersama.setText(fasilitas_bersama.get(position));
        }

        return convertView;
    }
}
