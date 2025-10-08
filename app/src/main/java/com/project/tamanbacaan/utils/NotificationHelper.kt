package com.caffeinatedr4t.tamanbacaan.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.caffeinatedr4t.tamanbacaan.R

object NotificationHelper {
    private const val CHANNEL_ID = "taman_bacaan_channel"
    private const val CHANNEL_NAME = "Notifikasi Utama Taman Bacaan"

    fun createNotificationChannel(context: Context) {
        // Harus dijalankan di API 26+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Saluran Notifikasi untuk Pemberitahuan Buku dan Kegiatan."
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, title: String, message: String, notificationId: Int) {
        // Pastikan channel sudah dibuat
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Teks panjang
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            notify(notificationId, builder.build())
        }
    }
}