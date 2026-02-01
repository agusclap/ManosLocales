package com.undef.manoslocales.ui.producto

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(4.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onVerDetallesClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CafeClaro.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = producto.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                
                IconButton(
                    onClick = { onFavoritoClicked(producto) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Crema.copy(alpha = 0.7f), RoundedCornerShape(50.dp))
                ) {
                    Icon(
                        imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorito) Cafe else GrisSuave,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = producto.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Cafe,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$${String.format("%.2f", producto.price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    ),
                    color = Cafe
                )
                
                Text(
                    text = producto.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisSuave,
                    maxLines = 1
                )
            }
        }
    }
}
