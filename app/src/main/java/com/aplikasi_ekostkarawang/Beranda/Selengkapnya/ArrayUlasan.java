package com.aplikasi_ekostkarawang.Beranda.Selengkapnya;

class ArrayUlasan {
    private String Isi_Ulasan, Nama_Akun, Rating, Tanggal;

    ArrayUlasan(String Isi_Ulasan, String Nama_Akun, String Rating, String Tanggal){
        this.Isi_Ulasan = Isi_Ulasan;
        this.Nama_Akun  = Nama_Akun;
        this.Rating     = Rating;
        this.Tanggal    = Tanggal;
    }

    public String getIsi_Ulasan() {
        return Isi_Ulasan;
    }

    public String getNama_Akun() {
        return Nama_Akun;
    }

    public String getRating() {
        return Rating;
    }

    public String getTanggal() {
        return Tanggal;
    }
}