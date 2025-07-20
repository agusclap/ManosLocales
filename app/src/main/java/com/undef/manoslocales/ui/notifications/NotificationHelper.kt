package com.undef.manoslocales.ui.notifications

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.dataclasses.Product

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showNovedadFavoritoNotification(context: Context, producto: Product) {
    val builder = NotificationCompat.Builder(context, "favoritos_channel")
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Nuevo producto de un productor favorito")
        .setContentText("${producto.name} ya está disponible por \$${producto.price}")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    NotificationManagerCompat.from(context).notify(
        producto.id.hashCode(), // ID único por producto
        builder.build()
    )
}

private fun Unit.setContentText(string: String) {
    TODO("Not yet implemented")
}

private fun ERROR.setContentTitle(string: String) {
    TODO("Not yet implemented")
}

annotation class ERROR
