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
import androidx.compose.ui.unit.dp
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
        containerColor = com.undef.manoslocales.ui.theme.Crema.copy(alpha = 0.95f), // Fondo crema translúcido
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.Home, 
                    contentDescription = "Home",
                    tint = if (selectedItem == 0) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave
                ) 
            },
            label = { 
                Text(
                    "Home", 
                    color = if (selectedItem == 0) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave,
                    fontWeight = if (selectedItem == 0) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelMedium
                ) 
            },
            selected = selectedItem == 0,
            onClick = {
                onItemSelected(0)
                if (navController.currentDestination?.route != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.undef.manoslocales.ui.theme.Cafe,
                selectedTextColor = com.undef.manoslocales.ui.theme.Cafe,
                indicatorColor = com.undef.manoslocales.ui.theme.Cafe.copy(alpha = 0.2f), // Indicador tonal café
                unselectedIconColor = com.undef.manoslocales.ui.theme.GrisSuave,
                unselectedTextColor = com.undef.manoslocales.ui.theme.GrisSuave
            )
        )

        // Favoritos
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Filled.Favorite, 
                    contentDescription = "Favoritos",
                    tint = if (selectedItem == 1) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave
                ) 
            },
            label = { 
                Text(
                    "Favoritos", 
                    color = if (selectedItem == 1) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave,
                    fontWeight = if (selectedItem == 1) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelMedium
                ) 
            },
            selected = selectedItem == 1,
            onClick = {
                onItemSelected(1)
                if (navController.currentDestination?.route != "favoritos") {
                    navController.navigate("favoritos") {
                        popUpTo("favoritos") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.undef.manoslocales.ui.theme.Cafe,
                selectedTextColor = com.undef.manoslocales.ui.theme.Cafe,
                indicatorColor = com.undef.manoslocales.ui.theme.Cafe.copy(alpha = 0.2f),
                unselectedIconColor = com.undef.manoslocales.ui.theme.GrisSuave,
                unselectedTextColor = com.undef.manoslocales.ui.theme.GrisSuave
            )
        )

        // Settings
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Filled.Settings, 
                    contentDescription = "Settings",
                    tint = if (selectedItem == 2) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave
                ) 
            },
            label = { 
                Text(
                    "Settings", 
                    color = if (selectedItem == 2) com.undef.manoslocales.ui.theme.Cafe else com.undef.manoslocales.ui.theme.GrisSuave,
                    fontWeight = if (selectedItem == 2) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelMedium
                ) 
            },
            selected = selectedItem == 2,
            onClick = {
                onItemSelected(2)
                if (navController.currentDestination?.route != "settings") {
                    navController.navigate("settings") {
                        popUpTo("settings") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.undef.manoslocales.ui.theme.Cafe,
                selectedTextColor = com.undef.manoslocales.ui.theme.Cafe,
                indicatorColor = com.undef.manoslocales.ui.theme.Cafe.copy(alpha = 0.2f),
                unselectedIconColor = com.undef.manoslocales.ui.theme.GrisSuave,
                unselectedTextColor = com.undef.manoslocales.ui.theme.GrisSuave
            )
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
