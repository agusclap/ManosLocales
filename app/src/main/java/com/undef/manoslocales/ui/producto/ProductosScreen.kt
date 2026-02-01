package com.undef.manoslocales.ui.producto

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.*

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

    var activeSearchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    ManosLocalesTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it },
                    navController = navController
                )
            },
            containerColor = CafeOscuro
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CafeOscuro)
                    .padding(paddingValues)
            ) {
                // Header con logo
                Image(
                    painter = painterResource(R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                // DockedSearchBar moderno
                @OptIn(ExperimentalMaterial3Api::class)
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { activeSearchQuery = searchQuery },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            "Buscar productos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisSuave
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Cafe
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = Cafe
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = Crema,
                        inputFieldColors = SearchBarDefaults.inputFieldColors(
                            focusedTextColor = Cafe,
                            unfocusedTextColor = Cafe,
                            focusedPlaceholderColor = GrisSuave,
                            unfocusedPlaceholderColor = GrisSuave
                        )
                    ),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp
                ) {
                    // Contenido cuando el SearchBar está activo
                    LazyColumn {
                        items(filteredList.take(5)) { producto ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        producto.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Cafe
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "$${String.format("%.2f", producto.price)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GrisSuave
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
                                        isSearchActive = false
                                    }
                            )
                        }
                    }
                }

                // Filtros mejorados
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryDropdown(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        categories = categories
                    )

                    // Filtros de ciudad y proveedor con diseño moderno
                    OutlinedTextField(
                        value = ciudad,
                        onValueChange = { ciudad = it },
                        label = {
                            Text(
                                "Ciudad",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Cafe,
                            unfocusedTextColor = Cafe,
                            focusedBorderColor = Cafe,
                            unfocusedBorderColor = CafeClaro.copy(alpha = 0.5f),
                            focusedLabelColor = Cafe,
                            unfocusedLabelColor = GrisSuave
                        )
                    )

                    OutlinedTextField(
                        value = proveedor,
                        onValueChange = { proveedor = it },
                        label = {
                            Text(
                                "Proveedor",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Cafe,
                            unfocusedTextColor = Cafe,
                            focusedBorderColor = Cafe,
                            unfocusedBorderColor = CafeClaro.copy(alpha = 0.5f),
                            focusedLabelColor = Cafe,
                            unfocusedLabelColor = GrisSuave
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Contenido principal
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Crema,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "Cargando productos...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Crema
                                )
                            }
                        }
                    }

                    filteredList.isEmpty() -> {
                        EmptyState(
                            title = "No se encontraron productos",
                            message = "Intenta ajustar tus filtros de búsqueda",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
private fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_emprendedores),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = GrisSuave.copy(alpha = 0.5f)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Crema,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisSuave,
                textAlign = TextAlign.Center
            )
        }
    }
}
