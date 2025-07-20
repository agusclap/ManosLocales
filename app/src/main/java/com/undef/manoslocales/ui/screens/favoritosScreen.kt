package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.producto.ItemProduct // ¡IMPORTACIÓN CLAVE para el ItemProduct unificado!
import com.undef.manoslocales.ui.proveedor.ProveedorItem
import kotlin.collections.isNotEmpty
import android.util.Log // Para depuración

@Composable
fun FavoritosScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel
) {
    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()
    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()

    Log.d("FAV_SCREEN", "FavoritosScreen recompuesta. Productos: ${productosFavoritos.size}, Proveedores: ${proveedoresFavoritos.size}")
    if (productosFavoritos.isNotEmpty()) {
        Log.d("FAV_SCREEN", "Productos Favoritos en Composable (contenido): ${productosFavoritos.map { it.name }}")
    } else {
        Log.d("FAV_SCREEN", "Productos Favoritos en Composable: ¡Lista vacía!")
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1, // Asumo que 1 es el índice de Favoritos
                onItemSelected = { /* manejar navegación, quizás navController.navigate(rutas[it]) */ },
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

            Text(
                text = "Mis Favoritos",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                if (productosFavoritos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Productos Favoritos",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(productosFavoritos) { producto ->
                        // Usando ItemProduct aquí:
                        ItemProduct(
                            producto = producto,
                            isFavorito = true, // Siempre es true en la pantalla de favoritos
                            onFavoritoClicked = { clickedProduct ->
                                Log.d("FAV_SCREEN", "Click en favorito en pantalla de favoritos para: ${clickedProduct.name}")
                                favoritosViewModel.toggleProductoFavorito(clickedProduct)
                            },
                            onVerDetallesClick = {
                                navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
                            }
                        )
                    }
                }

                if (proveedoresFavoritos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Proveedores Favoritos",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(proveedoresFavoritos) { proveedor ->
                        ProveedorItem(
                            proveedor = proveedor,
                            isFavorito = true, // Siempre es true en la pantalla de favoritos
                            onFavoritoClicked = {
                                favoritosViewModel.toggleProveedorFavorito(it)
                            },
                            onVerDetallesClick = {
                                navController.navigate("proveedorDetalle/${proveedor.email}")
                            }
                        )
                    }

                } else if (productosFavoritos.isEmpty() && proveedoresFavoritos.isEmpty()) {
                    item {
                        Text(
                            text = "No tienes favoritos aún. ¡Explora y añade algunos!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}