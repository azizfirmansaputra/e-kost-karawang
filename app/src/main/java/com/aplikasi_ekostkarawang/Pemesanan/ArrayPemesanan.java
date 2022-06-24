package com.aplikasi_ekostkarawang.Pemesanan;

import java.util.ArrayList;

class ArrayPemesanan {
    private String Nama_Pemesan, Email_Pemesan, Nomor_Pemesanan, Status_Pemesanan, Tanggal_Masuk, Nomor_Pembayaran, Metode_Pembayaran;
    private String ID_Kost, Nama_Kost, Nama_Kamar, Sewa_Per, Harga_Kost, Alamat_Kost;
    private ArrayList<String> arrayFotoKost;

    ArrayPemesanan(String Nama_Pemesan, String Email_Pemesan, String Nomor_Pemesanan, String Status_Pemesanan, String Tanggal_Masuk, String Nomor_Pembayaran,
                   String Metode_Pembayaran, String ID_Kost, String Nama_Kost, String Nama_Kamar, String Sewa_Per, String Harga_Kost, String Alamat_Kost,
                   ArrayList<String> arrayFotoKost){
        this.Nama_Pemesan       = Nama_Pemesan;
        this.Email_Pemesan      = Email_Pemesan;
        this.Nomor_Pemesanan    = Nomor_Pemesanan;
        this.Status_Pemesanan   = Status_Pemesanan;
        this.Tanggal_Masuk      = Tanggal_Masuk;
        this.Nomor_Pembayaran   = Nomor_Pembayaran;
        this.Metode_Pembayaran  = Metode_Pembayaran;
        this.ID_Kost            = ID_Kost;
        this.Nama_Kost          = Nama_Kost;
        this.Nama_Kamar         = Nama_Kamar;
        this.Sewa_Per           = Sewa_Per;
        this.Harga_Kost         = Harga_Kost;
        this.Alamat_Kost        = Alamat_Kost;
        this.arrayFotoKost      = arrayFotoKost;
    }

    public String getNama_Pemesan() {
        return Nama_Pemesan;
    }

    public String getEmail_Pemesan() {
        return Email_Pemesan;
    }

    public String getNomor_Pemesanan() {
        return Nomor_Pemesanan;
    }

    public String getStatus_Pemesanan() {
        return Status_Pemesanan;
    }

    public String getTanggal_Masuk() {
        return Tanggal_Masuk;
    }

    public String getNomor_Pembayaran() {
        return Nomor_Pembayaran;
    }

    public String getMetode_Pembayaran() {
        return Metode_Pembayaran;
    }

    public String getID_Kost() {
        return ID_Kost;
    }

    public String getNama_Kost() {
        return Nama_Kost;
    }

    public String getNama_Kamar() {
        return Nama_Kamar;
    }

    public String getSewa_Per() {
        return Sewa_Per;
    }

    public String getHarga_Kost() {
        return Harga_Kost;
    }

    public String getAlamat_Kost() {
        return Alamat_Kost;
    }

    public ArrayList<String> getArrayFotoKost() {
        return arrayFotoKost;
    }

    public void setTanggal_Masuk(String tanggal_Masuk) {
        Tanggal_Masuk = tanggal_Masuk;
    }

    public void setStatus_Pemesanan(String status_Pemesanan) {
        Status_Pemesanan = status_Pemesanan;
    }

    public void setMetode_Pembayaran(String metode_Pembayaran) {
        Metode_Pembayaran = metode_Pembayaran;
    }

    public void setNomor_Pembayaran(String nomor_Pembayaran) {
        Nomor_Pembayaran = nomor_Pembayaran;
    }
}