package com.undef.manoslocales.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // El color surface de este MaterialTheme local controla el fondo del 'recorte' del label
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Crema)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { 
                    Text(
                        "Categoría", 
                        style = TextStyle(
                            color = Crema,
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(color = Cafe, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Crema,
                    unfocusedContainerColor = Crema,
                    focusedTextColor = Cafe,
                    unfocusedTextColor = Cafe,
                    focusedBorderColor = Cafe,
                    unfocusedBorderColor = Cafe,
                    focusedLabelColor = Cafe,
                    unfocusedLabelColor = Cafe,
                    focusedTrailingIconColor = Cafe,
                    unfocusedTrailingIconColor = Cafe
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Crema)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = Cafe, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        onClick = { onCategorySelected(category); expanded = false }
                    )
                }
            }
        }
    }
}
