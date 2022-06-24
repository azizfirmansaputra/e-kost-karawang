package com.aplikasi_ekostkarawang.Lain;

import java.util.ArrayList;

public class ArrayAkun {
    private String ID_Akun, Nama_Lengkap, Email, Foto_Profil, Token;
    private ArrayList<String> arrayNotifikasi;

    public ArrayAkun(String ID_Akun, String Nama_Lengkap, String Email, String Foto_Profil, String Token, ArrayList<String> arrayNotifikasi) {
        this.ID_Akun            = ID_Akun;
        this.Nama_Lengkap       = Nama_Lengkap;
        this.Email              = Email;
        this.Foto_Profil        = Foto_Profil;
        this.Token              = Token;
        this.arrayNotifikasi    = arrayNotifikasi;
    }

    public String getID_Akun() {
        return ID_Akun;
    }

    public void setID_Akun(String ID_Akun) {
        this.ID_Akun = ID_Akun;
    }

    public String getNama_Lengkap() {
        return Nama_Lengkap;
    }

    public void setNama_Lengkap(String nama_Lengkap) {
        Nama_Lengkap = nama_Lengkap;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFoto_Profil() {
        return Foto_Profil;
    }

    public void setFoto_Profil(String foto_Profil) {
        Foto_Profil = foto_Profil;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public ArrayList<String> getArrayNotifikasi() {
        return arrayNotifikasi;
    }

    public void setArrayNotifikasi(ArrayList<String> arrayNotifikasi) {
        this.arrayNotifikasi = arrayNotifikasi;
    }
}