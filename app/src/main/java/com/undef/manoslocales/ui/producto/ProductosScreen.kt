package com.undef.manoslocales.ui.producto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search // Importar el icono de búsqueda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Importar clip para las esquinas redondeadas de la imagen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.users.Producto
import com.undef.manoslocales.ui.theme.ManosLocalesTheme


val productosList = listOf(
    Producto(1, "Vaso de cerámica", "Hecho a mano", "Artesanías", R.drawable.empendedoresimage1),
    Producto(2, "Bufanda tejida", "100% lana", "Textiles", R.drawable.emprendedoresimage2),
    Producto(3, "Dulce de leche", "Envase 250g", "Alimentos", R.drawable.empendedoresimage1),
    Producto(4, "Porta velas", "Decoración artesanal", "Artesanías", R.drawable.emprendedoresimage2),
    Producto(5, "Camiseta estampada", "Diseño local", "Textiles", R.drawable.emprendedoresimage2),
    Producto(6, "Aceite de oliva", "Botella 500ml", "Alimentos", R.drawable.empendedoresimage1)
)

@Composable
fun ProductosScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }

    val favoritos by favoritosViewModel.productosFavoritos.collectAsState()
    val categories = listOf("Todas", "Artesanías", "Textiles", "Alimentos")

    val filteredList = productosList.filter { producto ->
        (selectedCategory == "Todas" || producto.categoria == selectedCategory) &&
                producto.nombre.contains(searchQuery, ignoreCase = true)
    }

    var selectedItem by remember { mutableStateOf(0) }

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
                // Imagen con esquinas redondeadas
                Image(
                    painter = painterResource(id = R.drawable.manoslocales),
                    contentDescription = "Logo Manos Locales",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .width(180.dp)
                        .offset(y = (-18).dp)
                        .clip(RoundedCornerShape(16.dp)), // Esquinas redondeadas para la imagen
                    contentScale = ContentScale.Fit // Ajustar la escala para que la imagen se vea bien
                )

                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = categories
                )

                Spacer(modifier = Modifier.height(20.dp))

                // TextField con esquinas redondeadas y icono de búsqueda
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar producto", color = Color(0xFFFEFAE0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp), // Esquinas redondeadas para el TextField
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
                        // Color de fondo del TextField para que contraste
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
                    items(filteredList) { producto ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ProductoItem(
                                producto = producto,
                                isFavorito = favoritos.any { it.id == producto.id },
                                onFavoritoClicked = { selected ->
                                    favoritosViewModel.toggleProductoFavorito(selected)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    isFavorito: Boolean,
    onFavoritoClicked: (Producto) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Mayor elevación para sombra
        shape = RoundedCornerShape(12.dp), // Esquinas más redondeadas
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C4033)) // Color de fondo diferente para la tarjeta
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
                    model = producto.imagenUrl,
                    contentDescription = "Imagen de ${producto.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically // Alinear verticalmente los elementos del Row
                ) {
                    TextButton(
                        onClick = { /* Ver detalles */ },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFEFAE0)) // Color de acento
                    ) {
                        Text(
                            text = "Ver detalles",
                            style = MaterialTheme.typography.bodyMedium,
                            // color = Color.White // Ya se define en contentColor del ButtonDefaults
                        )
                    }

                    IconButton(onClick = { onFavoritoClicked(producto) }) {
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
