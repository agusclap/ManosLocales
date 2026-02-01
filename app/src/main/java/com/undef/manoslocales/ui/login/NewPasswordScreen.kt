package com.undef.manoslocales.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeOscuro
import com.undef.manoslocales.ui.theme.Crema

@Composable
fun NewPasswordScreen(
    email: String,
    code: String,
    viewModel: UserViewModel,
    onSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                Text("Nueva Contraseña", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Cafe)
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (newPassword.length >= 8) {
                            viewModel.resetPasswordWithCode(email, code, newPassword) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) onSuccess()
                            }
                        } else {
                            Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cafe, contentColor = Crema),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ACTUALIZAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
