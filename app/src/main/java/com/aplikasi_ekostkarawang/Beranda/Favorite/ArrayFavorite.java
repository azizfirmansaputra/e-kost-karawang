package com.aplikasi_ekostkarawang.Beranda.Favorite;

class ArrayFavorite {
    private String Nama_Lengkap, Email, Foto_Profil;

    ArrayFavorite(String Nama_Lengkap, String Email, String Foto_Profil){
        this.Nama_Lengkap   = Nama_Lengkap;
        this.Email          = Email;
        this.Foto_Profil    = Foto_Profil;
    }

    String getNama_Lengkap() {
        return Nama_Lengkap;
    }

    String getEmail() {
        return Email;
    }

    String getFoto_Profil() {
        return Foto_Profil;
    }
}