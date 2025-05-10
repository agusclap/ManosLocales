package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.undef.manoslocales.R

@Composable
fun HomeScreen(
    navController: NavHostController,
    onEmprendedoresClick: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = {
                    selectedItem = it
                    when (it) {
                        0 -> navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                        1 -> navController.navigate("emprendedores") {
                            popUpTo("emprendedores") { inclusive = true }
                        }
                        2 -> navController.navigate("register") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
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

            EmprendedoresCard(onClick = {
                onEmprendedoresClick()
            })
            Spacer(modifier = Modifier.height(20.dp))
            ProveedoresCard()
            Spacer(modifier = Modifier.height(20.dp))
            PerfilButton()
        }
    }
}


@Composable
fun EmprendedoresCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp)
            .offset(y = (-30).dp)
            .clickable { onClick() }, // Acción de clic en el card
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
                contentDescription = "Icono Emprendedores",
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
fun ProveedoresCard() {
    Card(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp)
            .offset(y = (-30).dp),
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
fun PerfilButton() {
    Button(
        onClick = { /* Acción para ir al perfil */ },
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
    onItemSelected: (Int) -> Unit
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
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = {
                Text("Profile", color = Color.Black, fontWeight = FontWeight.Bold)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()  // NavController falso para la preview
    ManosLocalesTheme {
        HomeScreen(
            navController = navController,
            onEmprendedoresClick = { /* Acción para ir a la pantalla de emprendedores */ }
        )
    }
}

