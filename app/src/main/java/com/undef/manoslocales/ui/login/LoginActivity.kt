package com.undef.manoslocales.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.undef.manoslocales.MainActivity
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManosLocalesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AuthNavGraph(
                        navController = navController,
                        onLoginSuccess = {
                            // Al loguearse con éxito, vamos a la MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Cerramos el Login para que no puedan volver atrás
                        }
                    )
                }
            }
        }
    }
}
