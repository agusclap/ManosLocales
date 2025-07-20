package com.undef.manoslocales.ui.producto

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.ManosLocalesTheme
import com.undef.manoslocales.util.showNotification

@Composable
fun ProductosScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val notificacionesHabilitadas = sharedPreferences.getBoolean("notificaciones_activadas", false)

    var selectedCategory by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItem by remember { mutableIntStateOf(0) }

    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()
    val categories = listOf("Todas", "Artesanías", "Textiles", "Alimentos")
    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var productosViejos by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getProducts { nuevosProductos ->
            if (notificacionesHabilitadas && productosViejos.isNotEmpty()) {
                val nuevosDeFavoritos = nuevosProductos.filter { nuevo ->
                    productosViejos.none { viejo -> viejo.id == nuevo.id } &&
                            productosFavoritos.any { favorito -> favorito.id == nuevo.id }
                }
                if (nuevosDeFavoritos.isNotEmpty()) {
                    showNotification(
                        context,
                        "¡Nuevas publicaciones!",
                        "Hay novedades de tus productores favoritos."
                    )
                }
            }
            productosViejos = productos
            productos = nuevosProductos
        }
    }

    val filteredList = productos.filter { producto ->
        (selectedCategory == "Todas" || producto.category.equals(selectedCategory, ignoreCase = true)) &&
                producto.name.contains(searchQuery, ignoreCase = true)
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

                Spacer(Modifier.height(20.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar producto", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFEFAE0))
                    },
                    colors = TextFieldDefaults.colors(
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
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff3E2C1C)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(filteredList) { producto ->
                        ProductoItemFirestore(
                            producto = producto,
                            isFavorito = false,
                            onFavoritoClicked = { /* favorito */ },
                            onVerDetalles = {
                                navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItemFirestore(
    producto: Product,
    isFavorito: Boolean,
    onFavoritoClicked: (Product) -> Unit,
    onVerDetalles: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C4033))
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
                    model = producto.imageUrl,
                    contentDescription = "Imagen de ${producto.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(producto.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(producto.description, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                Text("Precio: $${producto.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onVerDetalles,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFEFAE0))
                    ) {
                        Text("Ver detalles")
                    }

                    IconButton(onClick = { onFavoritoClicked(producto) }) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = Color(0xffFEFAE0)
                        )
                    }
                }
            }
        }
    }
}
