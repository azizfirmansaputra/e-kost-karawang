package com.aplikasi_ekostkarawang.Profil.KelolaKost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.aplikasi_ekostkarawang.R;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;

class AdapterFasilitas extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> arrayList;

    AdapterFasilitas(Context context, ArrayList<String> arrayList){
        this.context    = context;
        this.arrayList  = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
    @SuppressLint({"InflateParams", "SetTextI18n"})
    @SuppressWarnings("ConstantConditions")
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null && inflater != null){
            convertView = inflater.inflate(R.layout.list_fasilitas_sekitar, null);
        }

        TextView txtListFasilitasSekitar    = convertView.findViewById(R.id.txtListFasilitasSekitar);
        Button btnHapusListFasilitasSekitar = convertView.findViewById(R.id.btnHapusListFasilitasSekitar);

        txtListFasilitasSekitar.setText("- " + arrayList.get(position));
        PushDownAnim.setPushDownAnimTo(btnHapusListFasilitasSekitar);

        btnHapusListFasilitasSekitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}