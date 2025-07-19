package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
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
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .offset(y = (-20).dp)
            )

            ProductosCard(onClick = { onProductosClick() })
            Spacer(modifier = Modifier.height(20.dp))
            ProveedoresCard(onClick = { onProveedoresClick() })
            Spacer(modifier = Modifier.height(20.dp))

            if (userRole == "provider") {
                Button(
                    onClick = { navController.navigate("misproductos") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(top = 16.dp)
                        .offset(y = (-30).dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffFEFAE0),
                        contentColor = Color(0xff3E2C1C)
                    )
                ) {
                    Text(
                        text = "Mis Productos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = onCreateProductClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(top = 16.dp)
                        .offset(y = (-30).dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffFEFAE0),
                        contentColor = Color(0xff3E2C1C)
                    )
                ) {
                    Text(
                        text = "Crear Producto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            PerfilButton(navController)
        }
    }
}

@Composable
fun ProductosCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp)
            .offset(y = (-30).dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEFAE0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_emprendedores),
                contentDescription = "Icono Productos",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 3.dp)
                    .offset(y = (2.5).dp)
            )
        }
    }
}

@Composable
fun ProveedoresCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp)
            .offset(y = (-30).dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEFAE0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_proveedores),
                contentDescription = "Icono Proveedores",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 3.dp)
                    .offset(y = (2.6).dp)
            )
        }
    }
}

@Composable
fun PerfilButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("profile") },
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(top = 16.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xffFEFAE0),
            contentColor = Color(0xff3E2C1C)
        )
    ) {
        Text(
            text = "My Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
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
