package com.undef.manoslocales.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeOscuro
import com.undef.manoslocales.ui.theme.Crema

@Composable
fun ResetVerificationScreen(
    email: String,
    viewModel: UserViewModel,
    onCodeVerified: (String) -> Unit, // Aunque ya no navegamos a otra pantalla, mantenemos la firma o ajustamos
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("¡Identidad confirmada!", color = Cafe, fontWeight = FontWeight.Bold) },
            text = { Text("Revisa tu email para el último paso de seguridad (enlace de restablecimiento).", color = Cafe) },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    onBack() 
                }) {
                    Text("ENTENDIDO", color = Cafe, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Crema,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CafeOscuro)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Crema),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Recuperación", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cafe)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ingresa el código enviado a $email", color = Cafe.copy(alpha = 0.7f), fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it },
                    label = { Text("Código") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (code.length == 6) {
                            isLoading = true
                            viewModel.validateResetCodeAndSendEmail(email, code) { success, message ->
                                isLoading = false
                                if (success) {
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, message ?: "Error desconocido", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Por favor, ingresa los 6 dígitos del código", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cafe, contentColor = Crema),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Crema, modifier = Modifier.size(24.dp))
                    } else {
                        Text("VALIDAR CÓDIGO", fontWeight = FontWeight.Bold)
                    }
                }
                
                TextButton(onClick = onBack, enabled = !isLoading) {
                    Text("Volver", color = Cafe)
                }
            }
        }
    }
}
