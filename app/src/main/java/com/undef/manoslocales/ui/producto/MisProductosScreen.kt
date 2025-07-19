package com.undef.manoslocales.ui.proveedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.database.UserViewModel


@Composable
fun MisProductoItem(
    producto: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C4033))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = producto.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Text(
                text = "Precio: $${producto.price}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Editar", color = Color(0xFFFFF5C0))
                }
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisProductosScreen(
    navController: NavHostController,
    viewModel: UserViewModel
) {
    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var productoAEliminar by remember { mutableStateOf<Product?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getMyProducts { productos = it }
    }

    if (showConfirmDialog && productoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar eliminación", color = Color.Black) },
            text = {
                Text(
                    "¿Seguro querés eliminar \"${productoAEliminar!!.name}\"?",
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(productoAEliminar!!.id) { success, _ ->
                        if (success) {
                            productos = productos.filter { it.id != productoAEliminar!!.id }
                        }
                    }
                    showConfirmDialog = false
                    productoAEliminar = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    productoAEliminar = null
                }) {
                    Text("Cancelar", color = Color.Black)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color(0xff3E2C1C),
        topBar = {
            TopAppBar(
                title = { Text("Mis Productos", color = Color(0xffFEFAE0)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xffFEFAE0)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors( // ✅ CORREGIDO
                    containerColor = Color(0xff3E2C1C)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
        ) {
            items(productos, key = { it.id }) { producto ->
                MisProductoItem( // ✅ IMPORT NECESARIO
                    producto = producto,
                    onEdit = {
                        navController.navigate("editarProducto/${producto.id}")
                    },
                    onDelete = {
                        productoAEliminar = producto
                        showConfirmDialog = true
                    }
                )
            }
        }
    }
}
