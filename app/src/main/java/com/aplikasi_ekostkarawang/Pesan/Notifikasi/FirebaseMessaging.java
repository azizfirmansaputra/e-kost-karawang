//package com.aplikasi_ekostkarawang.Pesan.Notifikasi;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.core.app.NotificationCompat;
//
//import com.aplikasi_ekostkarawang.Pesan.ActivityPesan;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class FirebaseMessaging extends FirebaseMessagingService {
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        String Mengirim = remoteMessage.getData().get("Mengirim");
//
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(firebaseUser != null && Mengirim.equals(firebaseUser.getUid())){
//            kirimNotifikasi(remoteMessage);
//        }
//    }
//
//    private void kirimNotifikasi(RemoteMessage remoteMessage) {
//        String Pengguna = remoteMessage.getData().get("Pengguna");
//        String Ikon     = remoteMessage.getData().get("Ikon");
//        String Judul    = remoteMessage.getData().get("Judul");
//        String Body     = remoteMessage.getData().get("Body");
//
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        int j           = Integer.parseInt(Pengguna.replaceAll("[\\D]", ""));
//        Intent intent   = new Intent(this, ActivityPesan.class);
//        Bundle bundle   = new Bundle();
//
//        bundle.putString("Pengguna", Pengguna);
//        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSong = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(Integer.parseInt(Ikon))
//                .setContentTitle(Judul)
//                .setAutoCancel(true)
//                .setContentText(Body)
//                .setSound(defaultSong)
//                .setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int i = 0;
//        if(j > 0){
//            i = j;
//        }
//
//        notificationManager.notify(i, builder.build());
//    }
//}