// NotificationUtils.kt
package com.undef.manoslocales.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.undef.manoslocales.R

fun showNotification(context: Context, title: String, content: String) {
    val channelId = "nuevos_productos_channel"

    // Crear canal de notificación si es necesario
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Nuevos Productos"
        val descriptionText = "Notificaciones sobre nuevos productos de tus favoritos"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
