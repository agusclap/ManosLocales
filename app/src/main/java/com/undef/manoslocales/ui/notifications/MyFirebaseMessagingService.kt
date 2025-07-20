package com.undef.manoslocales.ui.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Se llama cuando se genera un nuevo token o se actualiza el existente.
    // ¡Es fundamental que envíes este token a tu backend!
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
        // TODO: Enviar este token a tu servidor para asociarlo con el usuario logueado.
        sendRegistrationToServer(token)
    }

    // Se llama cuando la app recibe un mensaje mientras está en primer plano.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Podés manejar los datos del mensaje aquí.
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Chequear si el mensaje contiene datos
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: " + remoteMessage.data)
        }

        // Chequear si el mensaje contiene una notificación
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            // Aquí creas y muestras la notificación localmente.
            sendLocalNotification(it.title, it.body)
        }
    }

    private fun sendLocalNotification(title: String?, body: String?) {
        // Lógica para construir y mostrar la notificación en la barra de estado del dispositivo.
        // Usá NotificationCompat.Builder para compatibilidad.
    }

    private fun sendRegistrationToServer(token: String?) {
        // Implementá la lógica para hacer una llamada a tu API y guardar el token.
    }
}