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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeOscuro
import com.undef.manoslocales.ui.theme.Crema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    email: String,
    uid: String,
    viewModel: UserViewModel,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }
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
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Crema),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verificación",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Cafe
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingresa el código enviado a\n$email",
                    fontSize = 14.sp,
                    color = Cafe.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it },
                    label = { Text("Código de 6 dígitos") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Cafe,
                        unfocusedTextColor = Cafe,
                        focusedBorderColor = Cafe,
                        unfocusedBorderColor = Cafe.copy(alpha = 0.5f),
                        focusedLabelColor = Cafe,
                        unfocusedLabelColor = Cafe.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (code.length == 6) {
                            viewModel.verifyCode(uid, code) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) onVerificationSuccess()
                            }
                        } else {
                            Toast.makeText(context, "Ingresa un código válido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cafe, contentColor = Crema)
                ) {
                    Text("VERIFICAR", fontWeight = FontWeight.Bold)
                }
                
                TextButton(onClick = onBack) {
                    Text("Volver", color = Cafe)
                }
            }
        }
    }
}
