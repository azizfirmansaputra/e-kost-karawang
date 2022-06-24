package com.aplikasi_ekostkarawang.Pesan;

public class ArrayPesan {
    private String Pengirim;
    private String Penerima;
    private String Pesan;
    private String Waktu;
    private String Tanggal;
    private long TimeStamp;
    private boolean Terkirim;

    public ArrayPesan(String Pengirim, String Penerima, String Pesan, String Waktu, String Tanggal, long TimeStamp, boolean Terkirim) {
        this.Pengirim   = Pengirim;
        this.Penerima   = Penerima;
        this.Pesan      = Pesan;
        this.Waktu      = Waktu;
        this.Tanggal    = Tanggal;
        this.TimeStamp  = TimeStamp;
        this.Terkirim   = Terkirim;
    }

    public ArrayPesan(){ }

    public String getPengirim() {
        return Pengirim;
    }

    public void setPengirim(String pengirim) {
        Pengirim = pengirim;
    }

    public String getPenerima() {
        return Penerima;
    }

    public void setPenerima(String penerima) {
        Penerima = penerima;
    }

    public String getPesan() {
        return Pesan;
    }

    public void setPesan(String pesan) {
        Pesan = pesan;
    }

    public String getWaktu() {
        return Waktu;
    }

    public void setWaktu(String waktu) {
        Waktu = waktu;
    }

    public boolean isTerkirim() {
        return Terkirim;
    }

    public void setTerkirim(boolean terkirim) {
        Terkirim = terkirim;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }
}
