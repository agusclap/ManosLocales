package com.undef.manoslocales.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numerotel by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") }
    var categoria by remember { mutableStateOf("") }

    val categorias = listOf("Tecnología", "Herramientas", "Alimentos")
    var expanded by remember { mutableStateOf(false) }

    val isFormValid = password.isNotBlank() && email.isNotBlank()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3E2C1C))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .width(180.dp)
                    .offset(y = (-95).dp)
            )

            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xffFEFAE0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = numerotel,
                onValueChange = { numerotel = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().background(Color.White),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Rol", color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "user", onClick = { role = "user" })
                Text("Usuario", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(selected = role == "provider", onClick = { role = "provider" })
                Text("Proveedor", color = Color.White)
            }

            if (role == "provider") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Categoría", color = Color.White)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .background(Color.White),
                        label = { Text("Seleccionar categoría") }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.contains("@") && password.length >= 8) {
                        viewModel.registerUser(
                            email = email,
                            password = password,
                            nombre = nombre,
                            apellido = apellido,
                            role = role,
                            phone = numerotel,
                            categoria = if (role == "provider") categoria else null,
                            ciudad = ciudad
                        )
                        Toast.makeText(context, "Registrado como $role", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess()
                    } else {
                        Toast.makeText(context, "Verificá tu email o contraseña", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffFEFAE0),
                    contentColor = Color(0xff3E2C1C)
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "¿Ya tenés cuenta? Iniciá sesión",
                textAlign = TextAlign.Center,
                color = Color(0xffFEFAE0),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginClick() }
            )
        }
    }
}
