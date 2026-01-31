package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    onProductosClick: () -> Unit,
    onProveedoresClick: () -> Unit,
    userViewModel: UserViewModel,
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

    LaunchedEffect(Unit) {
        userViewModel.getUserRole { role ->
            userRole = role
        }
    }

        Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }

                        1 -> navController.navigate("favoritos") {
                            popUpTo("favoritos") { inclusive = true }
                            launchSingleTop = true
                        }

                        2 -> navController.navigate("settings") {
                            popUpTo("settings") { inclusive = true }
                            launchSingleTop = true
                        }
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo mejorado
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )

            // Cards de navegación principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProductosCard(
                    onClick = { onProductosClick() },
                    modifier = Modifier.weight(1f)
                )
                ProveedoresCard(
                    onClick = { onProveedoresClick() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Botones para proveedores
            if (userRole == "provider") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("misproductos") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = com.undef.manoslocales.ui.theme.Crema,
                        contentColor = com.undef.manoslocales.ui.theme.Cafe,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Mis Productos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    ExtendedFloatingActionButton(
                        onClick = onCreateProductClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = com.undef.manoslocales.ui.theme.Crema,
                        contentColor = com.undef.manoslocales.ui.theme.Cafe,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Crear Producto",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            PerfilButton(navController)
        }
    }
}

@Composable
fun ProductosCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(200.dp)
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                },
                onClickLabel = "Ver productos"
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.undef.manoslocales.ui.theme.Crema
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = com.undef.manoslocales.ui.theme.CafeClaro.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_emprendedores),
                contentDescription = "Icono Productos",
                tint = com.undef.manoslocales.ui.theme.Cafe,
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Productos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = com.undef.manoslocales.ui.theme.Cafe
            )
        }
    }
}

@Composable
fun ProveedoresCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(200.dp)
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                },
                onClickLabel = "Ver proveedores"
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.undef.manoslocales.ui.theme.Crema
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = com.undef.manoslocales.ui.theme.CafeClaro.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_proveedores),
                contentDescription = "Icono Proveedores",
                tint = com.undef.manoslocales.ui.theme.Cafe,
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Proveedores",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = com.undef.manoslocales.ui.theme.Cafe
            )
        }
    }
}

@Composable
fun PerfilButton(navController: NavHostController) {
    ExtendedFloatingActionButton(
        onClick = { navController.navigate("profile") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = com.undef.manoslocales.ui.theme.Crema,
        contentColor = com.undef.manoslocales.ui.theme.Cafe,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

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
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = {
                Text("Home", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorite") },
            label = {
                Text("Favoritos", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = {
                Text("Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        )
    }
}
