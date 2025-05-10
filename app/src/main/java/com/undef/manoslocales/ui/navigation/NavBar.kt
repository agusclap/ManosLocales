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
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", color = Color.Black, fontWeight = FontWeight.Bold) },
            selected = selectedItem == 0,
            onClick = {
                onItemSelected(0)
                if (navController.currentDestination?.route != "home") {
                    navController.navigate("home") {
                        popUpTo(0) // limpia el stack si querés, opcional
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                onItemSelected(1)
                if (navController.currentDestination?.route != "home") {
                    navController.navigate("home") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "A CONFIGURAR") },
            label = {
                Text("A CONFIGURAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                onItemSelected(2)
                if (navController.currentDestination?.route != "settings") {
                    navController.navigate("settings") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = {
                Text("Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
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


