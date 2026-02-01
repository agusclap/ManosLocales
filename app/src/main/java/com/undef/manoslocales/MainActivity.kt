package com.undef.manoslocales

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.undef.manoslocales.ui.navigation.AppNavGraph
import com.undef.manoslocales.ui.notifications.NotificationHelper
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

class MainActivity : ComponentActivity() {

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicializar Canales de Notificación (Requerido por la cátedra)
        NotificationHelper.createNotificationChannels(this)

        // Registrar launchers de permisos
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) Log.d("MainActivity", "Permiso de ubicación concedido")
        }

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) Log.d("MainActivity", "Permiso de notificaciones concedido")
        }

        // Verificar y solicitar permisos en Runtime (Lineamientos Base PDF)
        checkAndRequestPermissions()

        setContent {
            ManosLocalesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        // Ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                // Según lineamientos: usar ActivityCompat si no se usa el launcher directo para cumplir con la cátedra
                // Aunque registerForActivityResult es lo moderno, nos aseguramos de seguir la lógica solicitada
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
