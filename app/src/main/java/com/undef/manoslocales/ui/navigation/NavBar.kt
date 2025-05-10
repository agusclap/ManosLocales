package com.undef.manoslocales.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFF3E2C1C)  // Fondo de la barra
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = {
                Text(
                    text = "Home",
                    color = if (selectedItem == 0) Color(0xFFFEFAE0) else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = {
                Text(
                    text = "Profile",
                    color = if (selectedItem == 1) Color(0xFFFEFAE0) else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = {
                Text(
                    text = "Settings",
                    color = if (selectedItem == 2) Color(0xFFFEFAE0) else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        selectedItem = 0,
        onItemSelected = {}
    )
}
