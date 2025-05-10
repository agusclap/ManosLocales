package com.undef.manoslocales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.undef.manoslocales.ui.navigation.AppNavGraph
import com.undef.manoslocales.ui.theme.LoginScreen
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManosLocalesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Aquí llamamos al Composable correctamente
                    // Aquí llamamos al Composable correctamente
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ManosLocalesTheme {
        Greeting("Android")
    }
}