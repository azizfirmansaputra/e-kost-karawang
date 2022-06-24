package com.aplikasi_ekostkarawang.Pesan;

import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Jawaban;
import com.aplikasi_ekostkarawang.Pesan.Notifikasi.Pengirim;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LayananAPI {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApv3intY:APA91bGMZx75poTqKrPtSbGQ00VPdtLyYjYonJWCh5JJfYdxnoL-cIJ8oZjLWLx_AGwSiIJlKqlUsZJ546UeEaqc-03RQ74GSXlVOTHwQ3IzDXzLvGUGOnqf70Pn56pWpXjuTvUqqL78"
            }
    )

    @POST("fcm/send")
    Call<Jawaban> kirimNotifikasi(@Body Pengirim body);
}