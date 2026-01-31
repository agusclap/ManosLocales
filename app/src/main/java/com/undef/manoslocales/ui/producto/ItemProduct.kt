package com.undef.manoslocales.ui.producto

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeClaro
import com.undef.manoslocales.ui.theme.Crema
import com.undef.manoslocales.ui.theme.GrisSuave

@Composable
fun ItemProduct(
    producto: Product,
    isFavorito: Boolean,
    onFavoritoClicked: (Product) -> Unit,
    onVerDetallesClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onVerDetallesClick()
                },
                onClickLabel = "Ver detalles de ${producto.name}"
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Crema
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CafeClaro.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto con aspectRatio(1f) y Clip(RoundedCornerShape(16.dp))
            Card(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = producto.imageUrl,
                    contentDescription = "Imagen de ${producto.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Título con headlineMedium Bold
                    Text(
                        text = producto.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Cafe,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Descripción con bodyMedium grisáceo
                    Text(
                        text = producto.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisSuave,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Precio destacado
                    Text(
                        text = "$${String.format("%.2f", producto.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Cafe,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onVerDetallesClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Cafe
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ver detalles",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    IconButton(
                        onClick = { onFavoritoClicked(producto) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorito) Cafe else GrisSuave,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}