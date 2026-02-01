package com.undef.manoslocales.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.undef.manoslocales.MainActivity
import com.undef.manoslocales.R

object NotificationHelper {

    const val CHANNEL_ID = "canal_precios"
    private const val CHANNEL_NAME = "Alertas de Manos Locales"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notificaciones sobre productos y proveedores favoritos"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendProductNotification(context: Context, productId: String, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para Deep Linking
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("PRODUCT_ID", productId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // TaskStackBuilder para navegación correcta hacia atrás
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(productId.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este recurso
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Expandible

        notificationManager.notify(productId.hashCode(), builder.build())
    }

    fun showPriceChangeNotification(context: Context, productId: String, productName: String, newPrice: Double, changeType: String) {
        val title = "¡Cambio de precio!"
        val message = "El precio de $productName $changeType a $$newPrice"
        sendProductNotification(context, productId, title, message)
    }

    fun showNewProductNotification(context: Context, productId: String, providerName: String, productName: String) {
        val title = "¡Nuevo producto!"
        val message = "$providerName ha subido un nuevo producto: $productName"
        sendProductNotification(context, productId, title, message)
    }
}
