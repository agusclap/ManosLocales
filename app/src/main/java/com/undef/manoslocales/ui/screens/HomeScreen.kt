package com.undef.manoslocales.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.notifications.NotificationViewModel
import com.undef.manoslocales.ui.producto.ItemProduct
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.Crema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onProductosClick: () -> Unit,
    onProveedoresClick: () -> Unit,
    userViewModel: UserViewModel,
    notificationViewModel: NotificationViewModel,
    favoritosViewModel: FavoritosViewModel,
    onCreateProductClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedItem = when (currentRoute) {
        "home" -> 0
        "favoritos" -> 1
        "settings" -> 2
        else -> 0
    }

    var userRole by remember { mutableStateOf<String?>(null) }
    var showNotifMenu by remember { mutableStateOf(false) }
    
    val notifications = notificationViewModel.notifications
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUserRole { role -> userRole = role }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.home_title), color = Crema) },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            showNotifMenu = !showNotifMenu
                            if (showNotifMenu) notificationViewModel.markAllAsRead()
                        }) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = Color.Red) {
                                            Text(unreadCount.toString(), color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = stringResource(id = R.string.notifications_desc), tint = Crema)
                            }
                        }
                        
                        DropdownMenu(
                            expanded = showNotifMenu,
                            onDismissRequest = { showNotifMenu = false },
                            modifier = Modifier.background(Cafe).width(250.dp)
                        ) {
                            if (notifications.isEmpty()) {
                                Text(
                                    stringResource(id = R.string.no_notifications),
                                    modifier = Modifier.padding(16.dp),
                                    color = Crema,
                                    fontSize = 14.sp
                                )
                            } else {
                                notifications.forEach { notif ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(notif.title, color = Crema, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                Text(notif.message, color = Crema.copy(alpha = 0.8f), fontSize = 12.sp)
                                            }
                                        },
                                        onClick = { 
                                            showNotifMenu = false
                                            navController.navigate("productoDetalle/${notif.productId}/unknown")
                                        }
                                    )
                                }
                                HorizontalDivider(color = Crema.copy(alpha = 0.2f))
                                TextButton(
                                    onClick = { notificationViewModel.clearNotifications() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(id = R.string.clear_all), color = Crema)
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Cafe)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true }
                        1 -> navController.navigate("favoritos") { popUpTo("favoritos") { inclusive = true }; launchSingleTop = true }
                        2 -> navController.navigate("settings") { popUpTo("settings") { inclusive = true }; launchSingleTop = true }
                    }
                },
                navController = navController
            )
        },
        containerColor = com.undef.manoslocales.ui.theme.CafeOscuro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )

            // --- SECCIÓN LAZY ROW DE FAVORITOS ---
            if (productosFavoritos.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Crema, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mis Favoritos",
                            color = Crema,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                    ) {
                        items(productosFavoritos) { producto ->
                            Box(modifier = Modifier.width(160.dp)) {
                                ItemProduct(
                                    producto = producto,
                                    isFavorito = true,
                                    onFavoritoClicked = { favoritosViewModel.toggleProductoFavorito(it) },
                                    onVerDetallesClick = {
                                        navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProductosCard(onClick = onProductosClick, modifier = Modifier.weight(1f))
                ProveedoresCard(onClick = onProveedoresClick, modifier = Modifier.weight(1f))
            }

            if (userRole == "provider") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("misproductos") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = Crema,
                        contentColor = Cafe
                    ) {
                        Text(stringResource(id = R.string.mis_productos), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }

                    ExtendedFloatingActionButton(
                        onClick = onCreateProductClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = Crema,
                        contentColor = Cafe
                    ) {
                        Text(stringResource(id = R.string.crear_producto), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
            PerfilButton(navController)
        }
    }
}

@Composable
fun ProductosCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, animationSpec = tween(150))
    Card(
        modifier = modifier.height(180.dp).scale(scale).clickable { isPressed = true; onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        border = BorderStroke(1.dp, com.undef.manoslocales.ui.theme.CafeClaro.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_emprendedores), contentDescription = null, tint = Cafe, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(id = R.string.productos), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Cafe)
        }
    }
}

@Composable
fun ProveedoresCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, animationSpec = tween(150))
    Card(
        modifier = modifier.height(180.dp).scale(scale).clickable { isPressed = true; onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        border = BorderStroke(1.dp, com.undef.manoslocales.ui.theme.CafeClaro.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_proveedores), contentDescription = null, tint = Cafe, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(id = R.string.proveedores), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Cafe)
        }
    }
}

@Composable
fun PerfilButton(navController: NavHostController) {
    ExtendedFloatingActionButton(
        onClick = { navController.navigate("profile") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = Crema,
        contentColor = Cafe
    ) {
        Text(stringResource(id = R.string.mi_perfil), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
    }
}
