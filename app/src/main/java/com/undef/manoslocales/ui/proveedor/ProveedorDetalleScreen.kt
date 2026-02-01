package com.undef.manoslocales.ui.proveedor

import android.content.Intent
import android.widget.Toast
import android.content.ActivityNotFoundException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedorDetalleScreen(
    providerId: String,
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onProviderClick: (String) -> Unit = {}
) {
    var proveedor by remember { mutableStateOf<User?>(null) }
    var productosProv by remember { mutableStateOf<List<Product>>(emptyList()) }
    var otrosProveedores by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    fun enviarCorreo(destinatario: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre tus productos")
            putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría saber más sobre tus productos publicados.")
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Enviar correo con..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay apps de correo instaladas", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(providerId) {
        viewModel.getUserById(providerId) { p ->
            proveedor = p
            p?.categoria?.let { cat ->
                viewModel.getProvidersByCategory(cat) { list ->
                    otrosProveedores = list.filter { it.id != providerId }
                }
            }
        }
        viewModel.getProductsByProvider(providerId) {
            productosProv = it
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Proveedor", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF3E2C1C))
            )
        },
        containerColor = Color(0xFF3E2C1C)
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF3E2C1C))
                    .padding(padding)
            ) {
                item {
                    proveedor?.let { p ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = p.profileImageUrl,
                                contentDescription = p.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .padding(4.dp)
                            )
                            Text(
                                text = p.nombre,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(p.email, color = Color.LightGray, fontSize = 14.sp)
                            Text(p.phone, color = Color.LightGray, fontSize = 14.sp)

                            Button(
                                onClick = { enviarCorreo(p.email) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBC6C25)),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Contactar por correo", color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Productos del proveedor",
                                color = Color(0xFFFFF5C0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                items(productosProv) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C4033)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = prod.imageUrl,
                                contentDescription = prod.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    prod.name, 
                                    color = Color.White, 
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("Precio: \$${prod.price}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                if (productosProv.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Este proveedor aún no publicó productos", color = Color.LightGray)
                        }
                    }
                }

                if (otrosProveedores.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            Text(
                                text = "Otros proveedores de ${proveedor?.categoria ?: ""}",
                                color = Color(0xFFFFF5C0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                items(otrosProveedores) { otherProv ->
                                    Card(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .clickable { onProviderClick(otherProv.id) },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C4033)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = otherProv.profileImageUrl,
                                                contentDescription = otherProv.nombre,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = otherProv.nombre,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
