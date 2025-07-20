package com.undef.manoslocales.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.undef.manoslocales.R

object NotificationHelper {

    private const val PRICE_CHANGE_CHANNEL_ID = "price_change_channel"
    private const val NEW_PRODUCT_CHANNEL_ID = "new_product_channel"
    private const val CHANNEL_NAME = "Alertas de ManosLocales"

    /**
     * Muestra una notificación local cuando el precio de un producto cambia.
     * ESTA ES LA FUNCIÓN QUE TE FALTA.
     *
     * @param context El contexto de la aplicación.
     * @param productName El nombre del producto que cambió de precio.
     * @param newPrice El nuevo precio del producto.
     * @param changeType Una cadena de texto como "subió" o "bajó".
     */
    fun showPriceChangeNotification(context: Context, productName: String, newPrice: Double, changeType: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(PRICE_CHANGE_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, PRICE_CHANGE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ¡Usa un ícono tuyo!
            .setContentTitle("¡Actualización de precio!")
            .setContentText("El precio de '$productName' ha cambiado. ¡Ahora $changeType a $$newPrice!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("El precio de tu producto favorito '$productName' ha cambiado. ¡Ahora $changeType a $$newPrice!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(productName.hashCode(), notification)
    }

    /**
     * Muestra una notificación cuando un proveedor favorito publica un nuevo producto.
     */
    fun showNewProductNotification(context: Context, providerName: String, productName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NEW_PRODUCT_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, NEW_PRODUCT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Nuevo producto de ${providerName}!")
            .setContentText(productName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(productName.hashCode(), notification)
    }
}
