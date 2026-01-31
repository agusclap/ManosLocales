package com.undef.manoslocales.ui.producto

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

@Composable
fun ProductosScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var selectedItem by remember { mutableIntStateOf(0) }

    val categories = listOf("Todas", "Tecnologia", "Herramientas", "Alimentos")

    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()

    val ciudadNormalized = ciudad.trim().lowercase()

    LaunchedEffect(selectedCategory, ciudadNormalized, proveedor) {
        isLoading = true

        if (proveedor.isNotBlank()) {
            viewModel.getProviderIdsByName(proveedor) { providerIds ->
                viewModel.getFilteredProducts(
                    categoria = if (selectedCategory == "Todas") null else selectedCategory,
                    ciudad = if (ciudadNormalized.isBlank()) null else ciudadNormalized,
                    proveedorId = null
                ) { productosEncontrados ->
                    productos = productosEncontrados.filter { it.providerId in providerIds }
                    isLoading = false
                }
            }
        } else {
            viewModel.getFilteredProducts(
                categoria = if (selectedCategory == "Todas") null else selectedCategory,
                ciudad = if (ciudadNormalized.isBlank()) null else ciudadNormalized,
                proveedorId = null
            ) {
                productos = it
                isLoading = false
            }
        }
    }

    val filteredList = productos.filter {
        it.name.contains(searchQuery, ignoreCase = true)
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
                    .background(Color(0xff3E2C1C))
                    .padding(paddingValues)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .offset(y = (-18).dp),
                    contentScale = ContentScale.Fit
                )

                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = categories
                )

                Spacer(Modifier.height(16.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar por nombre de producto", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFEFAE0))
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = getSearchColors()
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    value = ciudad,
                    onValueChange = { ciudad = it }, // <-- no normalices acá
                    label = { Text("Buscar por ciudad", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = getSearchColors()
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    value = proveedor,
                    onValueChange = { proveedor = it },
                    label = { Text("Buscar por proveedor", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = getSearchColors()
                )

                Spacer(Modifier.height(16.dp))

                when {
                    isLoading -> {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Cargando productos...", color = Color(0xFFFEFAE0))
                    }

                    filteredList.isEmpty() -> {
                        Text(
                            text = "No hay resultados con esos filtros.",
                            color = Color(0xFFFEFAE0)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(filteredList) { producto ->
                                val isFavorito = productosFavoritos.any { it.id == producto.id }

                                ItemProduct(
                                    producto = producto,
                                    isFavorito = isFavorito,
                                    onFavoritoClicked = {
                                        favoritosViewModel.toggleProductoFavorito(it)
                                    },
                                    onVerDetallesClick = {
                                        navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
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
private fun getSearchColors() = TextFieldDefaults.colors(
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
