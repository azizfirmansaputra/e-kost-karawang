package com.aplikasi_ekostkarawang.Lain;

import com.google.android.gms.maps.model.LatLng;

public class HitungJarak {

    private double Jarak(LatLng lokasiTerdekat, LatLng cariLokasi){
        double longDiff = lokasiTerdekat.longitude - cariLokasi.longitude;
        double Jarak    = Math.sin(Math.toRadians(lokasiTerdekat.latitude)) * Math.sin(Math.toRadians(cariLokasi.latitude)) +
                            Math.cos(Math.toRadians(lokasiTerdekat.latitude)) * Math.cos(Math.toRadians(cariLokasi.latitude)) *
                                Math.cos(Math.toRadians(longDiff));
        Jarak           = Math.acos(Jarak);
        Jarak           = Math.toDegrees(Jarak);
        Jarak           = Jarak * 60 * 1.1515;
        Jarak           = Jarak * 1.609344;

        return Jarak;
    }
}
