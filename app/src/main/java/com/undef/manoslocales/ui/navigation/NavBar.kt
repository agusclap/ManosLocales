package com.undef.manoslocales.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavHostController
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xffFEFAE0)
    ) {
        // Home
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", color = Color.Black, fontWeight = FontWeight.Bold) },
            selected = selectedItem == 0,
            onClick = {
                onItemSelected(0)
                if (navController.currentDestination?.route != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    // Si ya está en Home, limpiamos el back stack
                    navController.popBackStack("home", inclusive = false)
                }
            }
        )

        // A Configurar (Profile)
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                onItemSelected(1)
                if (navController.currentDestination?.route != "profile") {
                    navController.navigate("profile") {
                        popUpTo("profile") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.popBackStack("profile", inclusive = false)
                }
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = {
                Text("Profile", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )

        // Settings
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                onItemSelected(2)
                if (navController.currentDestination?.route != "settings") {
                    navController.navigate("settings") {
                        popUpTo("settings") { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.popBackStack("settings", inclusive = false)
                }
            },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = {
                Text("Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
    }

    // Listener para cambiar el estado del ítem seleccionado
    navController.addOnDestinationChangedListener { _, destination, _ ->
        when (destination.route) {
            "home" -> onItemSelected(0)
            "profile" -> onItemSelected(1)
            "settings" -> onItemSelected(2)
        }
    }
}





@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController() // NavController "falso" para el preview
    BottomNavigationBar(
        selectedItem = 0,
        onItemSelected = {},
        navController = navController
    )
}


