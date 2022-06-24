package com.aplikasi_ekostkarawang.Pesan.Notifikasi;

public class Data {
    private String Pengguna;
    private int Ikon;
    private String Body;
    private String Judul;
    private String Mengirim;

    public Data(String pengguna, int ikon, String body, String judul, String mengirim) {
        this.Pengguna   = pengguna;
        this.Ikon       = ikon;
        this.Body       = body;
        this.Judul      = judul;
        this.Mengirim   = mengirim;
    }

    public Data() {
    }

    public String getPengguna() {
        return Pengguna;
    }

    public void setPengguna(String pengguna) {
        Pengguna = pengguna;
    }

    public int getIkon() {
        return Ikon;
    }

    public void setIkon(int ikon) {
        Ikon = ikon;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getJudul() {
        return Judul;
    }

    public void setJudul(String judul) {
        Judul = judul;
    }

    public String getMengirim() {
        return Mengirim;
    }

    public void setMengirim(String mengirim) {
        Mengirim = mengirim;
    }
}