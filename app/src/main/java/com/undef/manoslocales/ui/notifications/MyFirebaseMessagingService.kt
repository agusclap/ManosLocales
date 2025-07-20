package com.undef.manoslocales.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.undef.manoslocales.MainActivity // Importa tu Activity principal
import com.undef.manoslocales.R // Importa tus recursos para el ícono

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "manos_locales_channel"
        private const val CHANNEL_NAME = "Notificaciones de ManosLocales"
    }

    /**
     * Se llama cuando se genera un nuevo token o se actualiza el existente.
     * Este token es la "dirección" única del dispositivo del usuario.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // Una vez que tenemos el token, lo enviamos a nuestro servidor (Firestore)
        // para asociarlo con el usuario que ha iniciado sesión.
        sendRegistrationToServer(token)
    }

    /**
     * Se llama cuando la app recibe un mensaje de FCM.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Chequeamos si el mensaje tiene una carga de notificación y la mostramos.
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification Title: ${notification.title}")
            Log.d(TAG, "Message Notification Body: ${notification.body}")
            sendLocalNotification(notification.title, notification.body)
        }
    }

    /**
     * Guarda el token de registro de FCM en el documento del usuario en Firestore.
     * Esto nos permite enviar notificaciones a usuarios específicos.
     *
     * @param token El nuevo token de FCM a guardar.
     */
    private fun sendRegistrationToServer(token: String?) {
        if (token == null) {
            Log.w(TAG, "FCM Token is null, cannot save to server.")
            return
        }

        // Obtenemos el ID del usuario que tiene la sesión iniciada.
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "User is not logged in, cannot save FCM token.")
            // Opcional: Podrías guardar el token localmente y subirlo después del login.
            return
        }

        // Creamos un mapa con el token para guardarlo en Firestore.
        val tokenData = hashMapOf(
            "fcmToken" to token
        )

        // Actualizamos el documento del usuario con el nuevo token.
        // Usamos 'update' en lugar de 'set' para no sobreescribir otros datos del usuario.
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .update(tokenData as Map<String, Any>)
            .addOnSuccessListener { Log.d(TAG, "FCM token updated successfully for user: $userId") }
            .addOnFailureListener { e -> Log.e(TAG, "Error updating FCM token", e) }
    }

    /**
     * Crea y muestra una notificación simple en la barra de estado del dispositivo.
     *
     * @param title El título de la notificación.
     * @param body El cuerpo del mensaje de la notificación.
     */
    private fun sendLocalNotification(title: String?, body: String?) {
        // Creamos un Intent que se ejecutará cuando el usuario toque la notificación.
        // En este caso, abrirá nuestra MainActivity.
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construimos la notificación.
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ¡IMPORTANTE! Usa un ícono tuyo.
            .setContentTitle(title ?: "Notificación")
            .setContentText(body)
            .setAutoCancel(true) // La notificación se cierra al tocarla.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Para Android 8.0 (API 26) y superior, es OBLIGATORIO registrar un canal de notificación.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Mostramos la notificación. El ID (0) debe ser único si quieres mostrar múltiples notificaciones.
        notificationManager.notify(0, notificationBuilder.build())
    }
}
