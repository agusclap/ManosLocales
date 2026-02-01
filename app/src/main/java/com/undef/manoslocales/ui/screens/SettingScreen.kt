package com.undef.manoslocales.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.Crema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val defaultCity by settingsViewModel.defaultCity.collectAsState()
    val priceNotificationsEnabled by settingsViewModel.priceNotificationsEnabled.collectAsState()
    val newProductNotificationsEnabled by settingsViewModel.newProductNotificationsEnabled.collectAsState()

    var expandedCity by remember { mutableStateOf(false) }
    val provincias = listOf(
        "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba",
        "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja",
        "Mendoza", "Misiones", "Neuquén", "Río Negro", "Salta", "San Juan",
        "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero",
        "Tierra del Fuego", "Tucumán"
    )

    val selectedItem = 2

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        1 -> navController.navigate("profile") { popUpTo("profile") { inclusive = true } }
                        2 -> navController.navigate("settings") { popUpTo("settings") { inclusive = true } }
                    }
                },
                navController = navController
            )
        },
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xffFEFAE0),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Preferencias de Búsqueda",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Start)
            )

            ExposedDropdownMenuBox(
                expanded = expandedCity,
                onExpandedChange = { expandedCity = !expandedCity },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = defaultCity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provincia por defecto para búsquedas") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCity,
                    onDismissRequest = { expandedCity = false },
                    modifier = Modifier.background(Crema)
                ) {
                    provincias.forEach { prov ->
                        DropdownMenuItem(
                            text = { Text(prov, color = Cafe) },
                            onClick = {
                                settingsViewModel.onDefaultCityChange(prov)
                                expandedCity = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Notificaciones",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
            )

            SwitchItem(
                title = "Alertas de cambio de precio",
                isChecked = priceNotificationsEnabled,
                onCheckedChange = { settingsViewModel.onPriceNotificationsChange(it) }
            )
            SwitchItem(
                title = "Alertas de nuevos productos",
                isChecked = newProductNotificationsEnabled,
                onCheckedChange = { settingsViewModel.onNewProductNotificationsChange(it) }
            )
        }
    }
}

@Composable
fun SwitchItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 16.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xffFEFAE0),
                uncheckedThumbColor = Color.Gray
            )
        )
    }
}
