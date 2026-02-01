package com.undef.manoslocales.ui.producto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedCity by remember { mutableStateOf("Todas") }
    var cityExpanded by remember { mutableStateOf(false) }
    var proveedor by remember { mutableStateOf("") }
    var selectedItem by remember { mutableIntStateOf(0) }

    val categories = listOf("Todas", "Tecnología", "Herramientas", "Alimentos")
    val provincias = listOf("Todas", "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba", "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones", "Neuquén", "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", "Tierra del Fuego", "Tucumán")

    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()
    val ciudadNormalized = if (selectedCity == "Todas") "" else selectedCity.trim().lowercase()

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

    val filteredList = productos.filter { it.name.contains(searchQuery, ignoreCase = true) }
    var isSearchActive by remember { mutableStateOf(false) }

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
                .padding(paddingValues)
                .padding(top = 8.dp) // Reducido padding superior
        ) {
            // Header Compacto
            Image(
                painter = painterResource(R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altura reducida
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            // Buscador Compacto
            @OptIn(ExperimentalMaterial3Api::class)
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp), // Espaciado reducido
                placeholder = { Text("Buscar...", fontSize = 14.sp, color = GrisSuave) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Cafe, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Cafe)
                        }
                    }
                },
                colors = SearchBarDefaults.colors(containerColor = Crema),
                shape = RoundedCornerShape(16.dp)
            ) {
                // Resultados de búsqueda...
            }

            // Filtros Compactos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado reducido
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryDropdown(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            categories = categories
                        )
                    }

                    // Selector de Provincia (Dropdown)
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = !cityExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedCity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Provincia", fontSize = 10.sp) }, // Fuente pequeña
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Cafe,
                                unfocusedTextColor = Cafe,
                                focusedBorderColor = Cafe,
                                unfocusedBorderColor = CafeClaro.copy(alpha = 0.5f),
                                focusedLabelColor = Cafe,
                                unfocusedLabelColor = GrisSuave
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false },
                            modifier = Modifier.background(Crema)
                        ) {
                            provincias.forEach { prov ->
                                DropdownMenuItem(
                                    text = { Text(prov, color = Cafe, fontSize = 14.sp) },
                                    onClick = { selectedCity = prov; cityExpanded = false }
                                )
                            }
                        }
                    }
                }
            }

            // Grilla de Productos (2 columnas)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Crema)
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos", color = Crema)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredList) { producto ->
                        val isFavorito = productosFavoritos.any { it.id == producto.id }
                        ItemProduct(
                            producto = producto,
                            isFavorito = isFavorito,
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
}
