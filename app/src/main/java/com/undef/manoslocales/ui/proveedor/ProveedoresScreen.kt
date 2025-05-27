package com.undef.manoslocales.ui.proveedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.theme.ManosLocalesTheme
import com.undef.manoslocales.ui.users.Proveedor
import com.undef.manoslocales.ui.navigation.FavoritosViewModel

// Lista de ejemplo para poblar la LazyColumn de proveedores
val proveedoresList = listOf(
    Proveedor(1, "Distribuidora XYZ", "Córdoba", "Tecnología", R.drawable.providersimage),
    Proveedor(2, "Proveedores ABC", "Buenos Aires", "Herramientas", R.drawable.providersimage),
    Proveedor(3, "Alimentos del Sur", "Mendoza", "Alimentos", R.drawable.providersimage)
)

@Composable
fun ProveedoresScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas", "Tecnología", "Herramientas", "Alimentos")
    var searchQuery by remember { mutableStateOf("") }
    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()

    var selectedItem by remember { mutableStateOf(1) } // 1 para que aparezca seleccionada la opción "Proveedores"

    val filteredList = if (selectedCategory == "Todas") {
        proveedoresList
    } else {
        proveedoresList.filter { it.categoria == selectedCategory }
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
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                            .width(180.dp)
                            .offset(y = (-18).dp)
                    )

                    Spacer(modifier = Modifier.height(0.dp))

                    CategoryDropdown(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        categories = categories
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    // TextField para la búsqueda de proveedores
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar proveedor", color = Color(0xFFFEFAE0)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
                        leadingIcon = { // Icono de búsqueda
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Icono de búsqueda",
                                tint = Color(0xFFFEFAE0)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFEFAE0),
                            focusedIndicatorColor = Color.Transparent, // Quitar la línea inferior al enfocar
                            unfocusedIndicatorColor = Color.Transparent, // Quitar la línea inferior sin enfocar
                            focusedLabelColor = Color(0xFFFEFAE0),
                            unfocusedLabelColor = Color.LightGray,
                            focusedContainerColor = Color(0xFF5C4033),
                            unfocusedContainerColor = Color(0xFF5C4033),
                            disabledContainerColor = Color(0xFF5C4033),
                            errorContainerColor = Color(0xFF5C4033),
                            focusedLeadingIconColor = Color(0xFFFEFAE0),
                            unfocusedLeadingIconColor = Color.LightGray,
                        )
                    )



                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xff3E2C1C)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(filteredList) { proveedor ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                ProveedorItem(
                                    proveedor = proveedor,
                                    isFavorito = proveedoresFavoritos.any { it.id == proveedor.id },
                                    onFavoritoClicked = { selectedProveedor ->
                                        favoritosViewModel.toggleProveedorFavorito(selectedProveedor)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProveedorItem(
    proveedor: Proveedor,
    isFavorito: Boolean,
    onFavoritoClicked: (Proveedor) -> Unit
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
            // Imagen
            Card(
                modifier = Modifier.size(115.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = proveedor.imagenUrl,
                    contentDescription = "Imagen de ${proveedor.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = proveedor.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = proveedor.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Text(
                    text = proveedor.ubicacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { /* Acción al hacer click en "Ver detalles" */ }
                    ) {
                        Text(
                            text = "Ver detalles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { onFavoritoClicked(proveedor) }
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Agregar a favoritos",
                            tint = Color(0xffFEFAE0)
                        )
                    }
                }
            }
        }
    }
}
