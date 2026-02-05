package com.undef.manoslocales.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.undef.manoslocales.MainActivity
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.login.LoginActivity
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(application)

        setContent {
            ManosLocalesTheme {
                SplashScreen(onTimeout = {
                    // Decidimos a dónde ir después del Splash
                    val nextActivity = if (sessionManager.isLoggedIn()) {
                        MainActivity::class.java
                    } else {
                        // Si NO está logueado, debe ir a LoginActivity obligatoriamente
                        LoginActivity::class.java
                    }
                    
                    startActivity(Intent(this@SplashActivity, nextActivity))
                    finish() // Cerramos el Splash para que no puedan volver atrás
                })
            }
        }
    }
}
