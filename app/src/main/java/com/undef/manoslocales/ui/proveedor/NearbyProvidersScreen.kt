package com.undef.manoslocales.ui.proveedor

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyProvidersScreen(viewModel: UserViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val listState = rememberLazyListState()
    
    var nearbyProviders by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedProviderId by remember { mutableStateOf<String?>(null) }
    val defaultCity by viewModel.defaultCityFlow.collectAsState(initial = "")

    // Tarea 2: Coordenadas de Respaldo (Fallback) para Argentina
    val provinceCapitals = remember {
        mapOf(
            "buenos aires" to LatLng(-34.9214, -57.9545),
            "caba" to LatLng(-34.6037, -58.3816),
            "cordoba" to LatLng(-31.4135, -64.1811),
            "santa fe" to LatLng(-31.6107, -60.6973),
            "mendoza" to LatLng(-32.8895, -68.8458),
            "tucuman" to LatLng(-26.8083, -65.2176),
            "salta" to LatLng(-24.7821, -65.4232),
            "jujuy" to LatLng(-24.1858, -65.2995),
            "misiones" to LatLng(-27.3671, -55.8961),
            "chaco" to LatLng(-27.4511, -58.9865),
            "corrientes" to LatLng(-27.4692, -58.8306),
            "entre rios" to LatLng(-31.7333, -60.5333),
            "san luis" to LatLng(-33.2950, -66.3356),
            "san juan" to LatLng(-31.5375, -68.5364),
            "la rioja" to LatLng(-29.4111, -66.8506),
            "catamarca" to LatLng(-28.4696, -65.7852),
            "santiago del estero" to LatLng(-27.7834, -64.2642),
            "formosa" to LatLng(-26.1849, -58.1731),
            "neuquen" to LatLng(-38.9516, -68.0591),
            "rio negro" to LatLng(-40.8135, -62.9967),
            "chubut" to LatLng(-43.3002, -65.1023),
            "santa cruz" to LatLng(-51.6226, -69.2181),
            "tierra del fuego" to LatLng(-54.8019, -68.3030),
            "la pampa" to LatLng(-36.6167, -64.2833)
        )
    }

    // Tarea 3: Centro geográfico de Argentina
    val argentinaCenter = LatLng(-38.4161, -63.6167)
    
    // Tarea 4: Ubicación inicial basada en la provincia por defecto o el centro del país
    val initialLocation = provinceCapitals[defaultCity.lowercase()] ?: argentinaCenter
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 5f)
    }

    // Estilo de mapa (Earth Tones)
    val mapStyle = remember {
        MapStyleOptions("""
            [
              {"elementType": "geometry", "stylers": [{"color": "#f5f1e6"}]},
              {"elementType": "labels.text.fill", "stylers": [{"color": "#523735"}]},
              {"elementType": "labels.text.stroke", "stylers": [{"color": "#f5f1e6"}]},
              {"featureType": "administrative", "elementType": "geometry.stroke", "stylers": [{"color": "#c9b2a6"}]},
              {"featureType": "landscape.natural", "elementType": "geometry", "stylers": [{"color": "#dfd2ae"}]},
              {"featureType": "poi", "elementType": "geometry", "stylers": [{"color": "#dfd2ae"}]},
              {"featureType": "road", "elementType": "geometry", "stylers": [{"color": "#f5f1e6"}]},
              {"featureType": "road.highway", "elementType": "geometry", "stylers": [{"color": "#f8c967"}]},
              {"featureType": "water", "elementType": "geometry.fill", "stylers": [{"color": "#b9d3c2"}]}
            ]
        """.trimIndent())
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fused.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchNearbyProviders(it.latitude, it.longitude) { providers ->
                        nearbyProviders = providers.sortedByDescending { p -> p.city?.equals(defaultCity, ignoreCase = true) == true }
                    }
                    scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 12f)) }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fused.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.fetchNearbyProviders(location.latitude, location.longitude) { providers ->
                        nearbyProviders = providers.sortedByDescending { p -> p.city?.equals(defaultCity, ignoreCase = true) == true }
                    }
                } else {
                    viewModel.getProviders { providers ->
                        nearbyProviders = providers.sortedByDescending { p -> p.city?.equals(defaultCity, ignoreCase = true) == true }
                    }
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            viewModel.getProviders { providers ->
                nearbyProviders = providers.sortedByDescending { p -> p.city?.equals(defaultCity, ignoreCase = true) == true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nearby_title), color = Crema, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cafe),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(id = R.string.back_button_desc), tint = Crema, modifier = Modifier.border(1.dp, Crema, CircleShape))
                    }
                }
            )
        },
        containerColor = CafeOscuro
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Mapa (40% de la pantalla)
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapStyleOptions = mapStyle,
                        isMyLocationEnabled = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true)
                ) {
                    nearbyProviders.forEach { provider ->
                        // Tarea 1: Asegurar que la búsqueda se haga en Argentina
                        val pos = if (provider.lat != 0.0) LatLng(provider.lat, provider.lng) 
                                 else provinceCapitals[provider.city?.lowercase()] ?: initialLocation
                        
                        Marker(
                            state = MarkerState(position = pos),
                            title = "${provider.nombre} ${provider.apellido}",
                            onClick = {
                                selectedProviderId = provider.id
                                scope.launch {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pos, 14f))
                                    val index = nearbyProviders.indexOf(provider)
                                    if (index >= 0) listState.animateScrollToItem(index)
                                }
                                true
                            }
                        )
                    }
                }
            }

            // Lista compacta (60% de la pantalla)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(0.6f).background(CafeOscuro).padding(top = 8.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(nearbyProviders) { _, provider ->
                    CompactProviderCard(
                        provider = provider,
                        isSelected = selectedProviderId == provider.id,
                        isDefaultProvince = provider.city?.equals(defaultCity, ignoreCase = true) == true,
                        onClick = {
                            selectedProviderId = provider.id
                            val pos = if (provider.lat != 0.0) LatLng(provider.lat, provider.lng) 
                                     else provinceCapitals[provider.city?.lowercase()] ?: initialLocation
                            scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pos, 15f)) }
                        },
                        onDetailClick = { navController.navigate("proveedorDetalle/${provider.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun CompactProviderCard(
    provider: User,
    isSelected: Boolean,
    isDefaultProvince: Boolean,
    onClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val bgColor by animateColorAsState(if (isSelected) Crema else Crema.copy(alpha = 0.85f))
    val borderColor = if (isSelected) Cafe else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de Perfil Circular
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(CafeClaro)) {
                if (provider.profileImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = provider.profileImageUrl,
                        contentDescription = stringResource(id = R.string.profile_pic_desc),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center).size(30.dp), tint = Cafe)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info Central
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${provider.nombre} ${provider.apellido}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Cafe,
                        maxLines = 1
                    )
                    if (isDefaultProvince) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Cafe,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.your_province_tag),
                                color = Crema,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Cafe.copy(alpha = 0.7f)
                    )
                    Text(
                        text = provider.city ?: stringResource(id = R.string.no_location),
                        style = MaterialTheme.typography.bodySmall,
                        color = Cafe.copy(0.8f)
                    )
                }
                
                Text(
                    text = provider.categoria ?: stringResource(id = R.string.default_category),
                    style = MaterialTheme.typography.labelSmall,
                    color = CafeClaro,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón de Acción Compacto
            FilledIconButton(
                onClick = onDetailClick,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Cafe),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(id = R.string.view_action), tint = Crema, modifier = Modifier.size(18.dp))
            }
        }
    }
}
