package com.undef.manoslocales.ui.proveedor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.User // Asegúrate que la ruta sea la correcta
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

@Composable
fun ProveedoresScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel,
    // CORRECCIÓN 1: Añadimos el UserViewModel como parámetro para poder usarlo.
    userViewModel: UserViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas", "Tecnologia", "Herramientas", "Alimentos")
    var searchQuery by remember { mutableStateOf("") }
    var searchCity by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(1) }

    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()
    var proveedores by remember { mutableStateOf<List<User>>(emptyList()) }

    // CORRECCIÓN 2: Llamamos a getProviders en la INSTANCIA 'userViewModel'.
    LaunchedEffect(Unit) {
        userViewModel.getProviders { listaDeProveedores ->
            proveedores = listaDeProveedores
        }
    }

    val filteredList = proveedores.filter {
        (selectedCategory == "Todas" || it.categoria == selectedCategory) &&
                it.nombre.contains(searchQuery, ignoreCase = true) &&
                (searchCity.isBlank() || it.city?.contains(searchCity, ignoreCase = true) == true)
    }

    ManosLocalesTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it },
                    navController = navController
                )
            },
            containerColor = Color(0xff3E2C1C)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xff3E2C1C))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .offset(y = (-18).dp)
                )

                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = categories
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar proveedor", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFEFAE0))
                    },
                    colors = searchFieldColors()
                )

                TextField(
                    value = searchCity,
                    onValueChange = { searchCity = it },
                    label = { Text("Buscar por ciudad", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFEFAE0))
                    },
                    colors = searchFieldColors()
                )

                Button(
                    onClick = { navController.navigate("nearby") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5C4033))
                ) {
                    Text("Ver proveedores cercanos", color = Color.White)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(filteredList) { proveedor ->
                        ProveedorItem(
                            proveedor = proveedor,
                            // CORRECCIÓN 3: Comparamos por ID, que es más robusto.
                            isFavorito = proveedoresFavoritos.any { it.id == proveedor.id },
                            onFavoritoClicked = { favoritosViewModel.toggleProveedorFavorito(proveedor) },
                            onVerDetallesClick = {
                                // CORRECCIÓN 4: Navegamos usando el ID (UID), que es más seguro.
                                navController.navigate("proveedorDetalle/${proveedor.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun searchFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFFFEFAE0),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = Color(0xFFFEFAE0),
    unfocusedLabelColor = Color.LightGray,
    focusedContainerColor = Color(0xFF5C4033),
    unfocusedContainerColor = Color(0xFF5C4033)
)

@Composable
fun ProveedorItem(
    proveedor: User,
    isFavorito: Boolean,
    onFavoritoClicked: (User) -> Unit,
    onVerDetallesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xff3E2C1C))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.size(115.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = proveedor.profileImageUrl,
                    contentDescription = "Imagen de ${proveedor.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(proveedor.nombre, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(proveedor.categoria ?: "Sin categoría", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                Text(proveedor.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                proveedor.city?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFEFAE0))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onVerDetallesClick) {
                        Text("Ver detalles", color = Color.White)
                    }
                    IconButton(onClick = { onFavoritoClicked(proveedor) }) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xffFEFAE0)
                        )
                    }
                }
            }
        }
    }
}
