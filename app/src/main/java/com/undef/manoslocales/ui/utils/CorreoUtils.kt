package com.undef.manoslocales.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun enviarCorreoAlProveedor(context: Context, destinatario: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
        putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre tus productos")
        putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría saber más sobre tus productos publicados.")
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Enviar correo con..."))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay apps de correo instaladas", Toast.LENGTH_SHORT).show()
    }
}
