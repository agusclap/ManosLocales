package com.undef.manoslocales.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyProvidersScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val fused = LocationServices.getFusedLocationProviderClient(context)
    var nearbyProviders by remember { mutableStateOf<List<User>>(emptyList()) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fused.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchNearbyProviders(it.latitude, it.longitude) { providers ->
                        nearbyProviders = providers
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fused.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchNearbyProviders(it.latitude, it.longitude) { providers ->
                        nearbyProviders = providers
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Proveedores cercanos") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (nearbyProviders.isEmpty()) {
                Text("No se encontraron proveedores cerca.")
            } else {
                nearbyProviders.forEach { provider ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "${provider.nombre} ${provider.apellido}", style = MaterialTheme.typography.titleMedium)
                            provider.city?.let {
                                Text(text = "Ciudad: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(text = "Tel: ${provider.phone}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
