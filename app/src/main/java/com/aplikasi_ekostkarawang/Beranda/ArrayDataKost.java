package com.aplikasi_ekostkarawang.Beranda;

import java.util.ArrayList;

public class ArrayDataKost {
    private String Nama_Kost, Deskripsi_Kost, Total_Kamar, Sisa_Kamar, Mayoritas_Penghuni, Harga_Kost_Hari;
    private String Harga_Kost_Bulan, Harga_Kost_Tahun, Peraturan_Kost, Pemilik, Kecamatan, Desa, Alamat, Jarak;
    private ArrayList<String> Nama_Kamar, Fasilitas_Bersama, Fasilitas_Sekitar, Dekat_Dengan, Foto_Kost, Video_Kost;

    public ArrayDataKost(String Nama_Kost, String Deskripsi_Kost, String Total_Kamar, String Sisa_Kamar, String Mayoritas_Penghuni, String Harga_Kost_Hari, String Harga_Kost_Bulan,
                  String Harga_Kost_Tahun, String Peraturan_Kost, String Pemilik, String Kecamatan, String Desa, String Alamat, String Jarak, ArrayList<String> Nama_Kamar,
                  ArrayList<String> Fasilitas_Bersama, ArrayList<String> Fasilitas_Sekitar, ArrayList<String> Dekat_Dengan, ArrayList<String> Foto_Kost,
                  ArrayList<String> Video_Kost){
        this.Nama_Kost          = Nama_Kost;
        this.Deskripsi_Kost     = Deskripsi_Kost;
        this.Total_Kamar        = Total_Kamar;
        this.Sisa_Kamar         = Sisa_Kamar;
        this.Mayoritas_Penghuni = Mayoritas_Penghuni;
        this.Harga_Kost_Hari    = Harga_Kost_Hari;
        this.Harga_Kost_Bulan   = Harga_Kost_Bulan;
        this.Harga_Kost_Tahun   = Harga_Kost_Tahun;
        this.Peraturan_Kost     = Peraturan_Kost;
        this.Pemilik            = Pemilik;
        this.Kecamatan          = Kecamatan;
        this.Desa               = Desa;
        this.Alamat             = Alamat;
        this.Jarak              = Jarak;
        this.Nama_Kamar         = Nama_Kamar;
        this.Fasilitas_Bersama  = Fasilitas_Bersama;
        this.Fasilitas_Sekitar  = Fasilitas_Sekitar;
        this.Dekat_Dengan       = Dekat_Dengan;
        this.Foto_Kost          = Foto_Kost;
        this.Video_Kost         = Video_Kost;
    }

    public String getNama_Kost() {
        return Nama_Kost;
    }

    public String getDeskripsi_Kost() {
        return Deskripsi_Kost;
    }

    public String getTotal_Kamar() {
        return Total_Kamar;
    }

    public String getSisa_Kamar() {
        return Sisa_Kamar;
    }

    public String getMayoritas_Penghuni() {
        return Mayoritas_Penghuni;
    }

    public String getHarga_Kost_Hari() {
        return Harga_Kost_Hari;
    }

    public String getHarga_Kost_Bulan() {
        return Harga_Kost_Bulan;
    }

    public String getHarga_Kost_Tahun() {
        return Harga_Kost_Tahun;
    }

    public String getPeraturan_Kost() {
        return Peraturan_Kost;
    }

    public String getPemilik() {
        return Pemilik;
    }

    public String getKecamatan() {
        return Kecamatan;
    }

    public String getDesa() {
        return Desa;
    }

    public String getAlamat() {
        return Alamat;
    }

    public String getJarak() {
        return Jarak;
    }

    public ArrayList<String> getNama_Kamar() {
        return Nama_Kamar;
    }

    public ArrayList<String> getFasilitas_Bersama() {
        return Fasilitas_Bersama;
    }

    public ArrayList<String> getFasilitas_Sekitar() {
        return Fasilitas_Sekitar;
    }

    public ArrayList<String> getDekat_Dengan() {
        return Dekat_Dengan;
    }

    public ArrayList<String> getFoto_Kost() {
        return Foto_Kost;
    }

    public ArrayList<String> getVideo_Kost() {
        return Video_Kost;
    }
}