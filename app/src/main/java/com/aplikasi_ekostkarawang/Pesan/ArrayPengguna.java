package com.aplikasi_ekostkarawang.Pesan;

class ArrayPengguna {
    private String Email;
    private String Foto_Profil;
    private String Nama_Lengkap;
    private String Status;

    public ArrayPengguna(String Email, String Foto_Profil, String Nama_Lengkap, String Status) {
        this.Email          = Email;
        this.Foto_Profil    = Foto_Profil;
        this.Nama_Lengkap   = Nama_Lengkap;
        this.Status         = Status;
    }

    public ArrayPengguna(){ }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFoto_Profil() {
        return Foto_Profil;
    }

    public void setFoto_Profil(String Foto_Profil) {
        Foto_Profil = Foto_Profil;
    }

    public String getNama_Lengkap() {
        return Nama_Lengkap;
    }

    public void setNama_Lengkap(String Nama_Lengkap) {
        Nama_Lengkap = Nama_Lengkap;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}