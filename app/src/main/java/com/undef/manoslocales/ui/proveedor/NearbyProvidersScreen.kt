package com.undef.manoslocales.ui.proveedor

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeOscuro
import com.undef.manoslocales.ui.theme.Crema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyProvidersScreen(viewModel: UserViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val fused = LocationServices.getFusedLocationProviderClient(context)
    var nearbyProviders by remember { mutableStateOf<List<User>>(emptyList()) }
    val defaultCity by viewModel.defaultCityFlow.collectAsState(initial = "Buenos Aires")

    // Coordenadas capitales por provincia (Simplificado para la tarea)
    val provinceCapitals = mapOf(
        "buenos aires" to LatLng(-34.9214, -57.9545),
        "cordoba" to LatLng(-31.4135, -64.1811),
        "santa fe" to LatLng(-31.6107, -60.6973),
        "mendoza" to LatLng(-32.8895, -68.8458),
        "tucuman" to LatLng(-26.8083, -65.2176)
    )

    val initialLocation = provinceCapitals[defaultCity.lowercase()] ?: LatLng(-34.6037, -58.3816) // Obelisco default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f)
    }

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
            TopAppBar(
                title = { Text("Proveedores cercanos", color = Crema) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cafe)
            )
        },
        containerColor = CafeOscuro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tarea 3: Mapa ocupa el 40% superior
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                ) {
                    nearbyProviders.forEach { provider ->
                        val providerLocation = if (provider.lat != 0.0 && provider.lng != 0.0) {
                            LatLng(provider.lat, provider.lng)
                        } else {
                            // Ubicación fija dentro de su provincia si no tiene coords (Tarea 2)
                            provinceCapitals[provider.city?.lowercase()] ?: LatLng(initialLocation.latitude + 0.01, initialLocation.longitude + 0.01)
                        }
                        
                        Marker(
                            state = MarkerState(position = providerLocation),
                            title = "${provider.nombre} ${provider.apellido}",
                            snippet = "Toca para ver detalles",
                            onInfoWindowClick = {
                                navController.navigate("proveedorDetalle/${provider.id}")
                            }
                        )
                    }
                }
            }

            // Tarea 3: Lista ocupa el resto
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (nearbyProviders.isEmpty()) {
                    item {
                        Text("No se encontraron proveedores cerca.", color = Crema)
                    }
                } else {
                    items(nearbyProviders) { provider ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("proveedorDetalle/${provider.id}") },
                            colors = CardDefaults.cardColors(containerColor = Crema.copy(alpha = 0.9f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${provider.nombre} ${provider.apellido}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Cafe,
                                    fontWeight = FontWeight.Bold
                                )
                                provider.city?.let {
                                    Text(text = "Ciudad: $it", style = MaterialTheme.typography.bodyMedium, color = Cafe.copy(alpha = 0.8f))
                                }
                                Text(text = "Tel: ${provider.phone}", style = MaterialTheme.typography.bodySmall, color = Cafe.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}
