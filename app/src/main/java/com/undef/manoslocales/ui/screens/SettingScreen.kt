package com.undef.manoslocales.ui.screens

import android.util.Log // Importante añadir el Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.undef.manoslocales.ui.navigation.BottomNavigationBar

@Composable
fun SettingScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // Observamos los StateFlows del ViewModel.
    val defaultCity by settingsViewModel.defaultCity.collectAsState()
    val priceNotificationsEnabled by settingsViewModel.priceNotificationsEnabled.collectAsState()
    val newProductNotificationsEnabled by settingsViewModel.newProductNotificationsEnabled.collectAsState()

    // --- LUZ DE INSPECCIÓN 1 ---
    // Este log nos dirá con qué valor se está dibujando la pantalla cada vez que se recompone.
    Log.d("SettingScreen", "Recomponiendo UI. Valores actuales -> Precios: $priceNotificationsEnabled, Nuevos Productos: $newProductNotificationsEnabled")

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

            // --- SECCIÓN DE PREFERENCIAS DE BÚSQUEDA ---
            Text(
                text = "Preferencias de Búsqueda",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Start)
            )
            OutlinedTextField(
                value = defaultCity,
                onValueChange = { settingsViewModel.onDefaultCityChange(it) },
                label = { Text("Ciudad por defecto para búsquedas") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECCIÓN DE NOTIFICACIONES ---
            Text(
                text = "Notificaciones",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
            )

            SwitchItem(
                title = "Alertas de cambio de precio",
                isChecked = priceNotificationsEnabled,
                onCheckedChange = { newCheckedState ->
                    // --- LUZ DE INSPECCIÓN 2 ---
                    // Este log nos dirá si el clic se está registrando y qué valor debería guardarse.
                    Log.d("SettingScreen", "Switch de PRECIOS clickeado. Nuevo estado debería ser: $newCheckedState")
                    settingsViewModel.onPriceNotificationsChange(newCheckedState)
                }
            )
            SwitchItem(
                title = "Alertas de nuevos productos",
                isChecked = newProductNotificationsEnabled,
                onCheckedChange = { newCheckedState ->
                    // --- LUZ DE INSPECCIÓN 3 ---
                    Log.d("SettingScreen", "Switch de NUEVOS PRODUCTOS clickeado. Nuevo estado debería ser: $newCheckedState")
                    settingsViewModel.onNewProductNotificationsChange(newCheckedState)
                }
            )
        }
    }
}


@Composable
fun SettingItem(title: String, value: String, actionText: String) { /* ... tu código ... */ }


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
