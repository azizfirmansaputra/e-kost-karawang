package com.aplikasi_ekostkarawang.Lain;

import java.util.ArrayList;

public class ArrayKost {
    private String ID_Kost, Nama_Kost, Alamat_Kost, Desa, Kecamatan, Jarak, Penghuni;
    private ArrayList<String> Foto_Kost, Video_Kost;

    public ArrayKost(String ID_Kost, String Nama_Kost, String Alamat_Kost, String Desa, String Kecamatan,
                     String Jarak, String Penghuni, ArrayList<String> Foto_Kost, ArrayList<String> Video_Kost){

        this.ID_Kost        = ID_Kost;
        this.Nama_Kost      = Nama_Kost;
        this.Alamat_Kost    = Alamat_Kost;
        this.Desa           = Desa;
        this.Kecamatan      = Kecamatan;
        this.Jarak          = Jarak;
        this.Penghuni       = Penghuni;
        this.Foto_Kost      = Foto_Kost;
        this.Video_Kost     = Video_Kost;
    }

    public String getID_Kost() {
        return ID_Kost;
    }

    public void setID_Kost(String ID_Kost) {
        this.ID_Kost = ID_Kost;
    }

    public String getNama_Kost() {
        return Nama_Kost;
    }

    public void setNama_Kost(String nama_Kost) {
        Nama_Kost = nama_Kost;
    }

    public String getAlamat_Kost() {
        return Alamat_Kost;
    }

    public void setAlamat_Kost(String alamat_Kost) {
        Alamat_Kost = alamat_Kost;
    }

    public String getDesa() {
        return Desa;
    }

    public void setDesa(String desa) {
        Desa = desa;
    }

    public String getKecamatan() {
        return Kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        Kecamatan = kecamatan;
    }

    public String getJarak() {
        return Jarak;
    }

    public void setJarak(String jarak) {
        Jarak = jarak;
    }

    public String getPenghuni() {
        return Penghuni;
    }

    public void setPenghuni(String penghuni) {
        Penghuni = penghuni;
    }

    public ArrayList<String> getFoto_Kost() {
        return Foto_Kost;
    }

    public void setFoto_Kost(ArrayList<String> foto_Kost) {
        Foto_Kost = foto_Kost;
    }

    public ArrayList<String> getVideo_Kost() {
        return Video_Kost;
    }

    public void setVideo_Kost(ArrayList<String> video_Kost) {
        Video_Kost = video_Kost;
    }
}