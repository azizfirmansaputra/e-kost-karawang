package com.aplikasi_ekostkarawang.Lain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aplikasi_ekostkarawang.Profil.KelolaKost.ActivityKelolaKost1;
import com.aplikasi_ekostkarawang.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Pesan " + remoteMessage.getNotification().getBody());
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            String title    = map.get("Judul");
            String message  = map.get("Pesan");
            String hisID    = map.get("IDAkun");
            String hisImage = map.get("Gambar");
            String chatID   = map.get("IDPesan");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title, message, hisID, hisImage, chatID);
            else
                normalNotifikasi(title, message, hisID, hisImage, chatID);
        } else Log.d("TAG", "onMessageReceived: no data ");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
        Log.d("FCM", "Token : " + s);
        super.onNewToken(s);
    }

    private void updateToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("E-KOST_KARAWANG", MODE_PRIVATE);
        Map<String, Object> map             = new HashMap<>();
        map.put("Token", token);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Pengguna")
                .child(sharedPreferences.getString("MasukSebagai", ""))
                .child(sharedPreferences.getString("IDAkun", ""));
        databaseReference.updateChildren(map);
    }

    private void normalNotifikasi(String Judul, String Pesan, String IDAkun, String Gambar, String IDPesan){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1000");
        builder.setContentTitle(Judul)
                .setContentText(Pesan)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .setSound(uri);

        Intent intent = new Intent(this, ActivityKelolaKost1.class);
        intent.putExtra("IDPesan", IDPesan);
        intent.putExtra("IDAkun", IDAkun);
        intent.putExtra("Gambar", Gambar);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85 - 65), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotification(String Judul, String Pesan, String IDAkun, String Gambar, String IDPesan) {

        NotificationChannel channel = new NotificationChannel("1000", "Pesan", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, ActivityKelolaKost1.class);
        intent.putExtra("IDPesan", IDPesan);
        intent.putExtra("IDAkun", IDAkun);
        intent.putExtra("Gambar", Gambar);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(this, "1000")
                .setContentTitle(Judul)
                .setContentText(Pesan)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(100, notification);
    }
}