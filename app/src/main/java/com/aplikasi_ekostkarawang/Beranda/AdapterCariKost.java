package com.aplikasi_ekostkarawang.Beranda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aplikasi_ekostkarawang.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class AdapterCariKost extends ArrayAdapter<ArrayDataKost> implements Filterable {
    private int resourceID;
    private List<ArrayDataKost> arrayDataKost, tempArray, arraySaran;

    AdapterCariKost(@NonNull Context context, int resourceID, List<ArrayDataKost> arrayDataKost) {
        super(context, resourceID, arrayDataKost);

        this.resourceID     = resourceID;
        this.arrayDataKost  = arrayDataKost;

        tempArray           = new ArrayList<>(arrayDataKost);
        arraySaran          = new ArrayList<>();
    }

    @NonNull
    @Override
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view               = LayoutInflater.from(parent.getContext()).inflate(resourceID, null, true);

        LinearLayout LLCariKost = view.findViewById(R.id.LLListFavorite);
        ImageView imgKost       = view.findViewById(R.id.imgFotoProfilListFavorite);
        TextView txtNamaKost    = view.findViewById(R.id.txtNamaAkunListFavorite);
        TextView txtAlamatKost  = view.findViewById(R.id.txtEmailAkunListFavorite);

        LLCariKost.setBackground(null);
        LLCariKost.setFocusable(false);
        LLCariKost.setClickable(false);

        if(arrayDataKost.get(position).getNama_Kost().equals(getContext().getString(R.string.Cari_Kost_Dekat_saya)) && arrayDataKost.get(position).getFoto_Kost() == null){
            imgKost.setImageDrawable(getContext().getDrawable(R.drawable.icon_lokasiku));
            txtNamaKost.setText(arrayDataKost.get(position).getNama_Kost());
            txtAlamatKost.setVisibility(View.GONE);
        }
        else{
            Glide.with(getContext()).asBitmap().load(arrayDataKost.get(position).getFoto_Kost().get(position)).into(imgKost);
            txtAlamatKost.setText(arrayDataKost.get(position).getAlamat() + ", " + arrayDataKost.get(position).getDesa());
            txtNamaKost.setText(arrayDataKost.get(position).getNama_Kost());
        }

        return view;
    }

    @Nullable
    @Override
    public ArrayDataKost getItem(int position) {
        return arrayDataKost.get(position);
    }

    @Override
    public int getCount() {
        return arrayDataKost.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return dataKostFilter;
    }

    private Filter dataKostFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null){
                arraySaran.clear();

                for(ArrayDataKost dataKost : tempArray){
                    if(dataKost.getNama_Kost().toLowerCase().contains(constraint.toString().toLowerCase())){
                        arraySaran.add(dataKost);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values        = arraySaran;
                filterResults.count         = arraySaran.size();

                return filterResults;
            }
            else{
                return new FilterResults();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<ArrayDataKost> tempValues = (ArrayList<ArrayDataKost>)results.values;

            if(results.count > 0){
                clear();

                for(ArrayDataKost dataKost : tempValues){
                    add(dataKost);
                }
                notifyDataSetChanged();
            }
            else{
                clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ArrayDataKost arrayDataKost = (ArrayDataKost)resultValue;
            return arrayDataKost.getNama_Kost();
        }
    };
}