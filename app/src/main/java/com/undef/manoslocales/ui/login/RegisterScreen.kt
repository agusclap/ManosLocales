package com.undef.manoslocales.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    var role by remember { mutableStateOf("user") } // 👈 campo de rol

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
                .wrapContentSize(Alignment.Center)
                .offset(y = (-40).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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

            // Inputs
            TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Name") },
                modifier = Modifier.fillMaxWidth().background(Color.White), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Lastname") },
                modifier = Modifier.fillMaxWidth().background(Color.White), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = numerotel, onValueChange = { numerotel = it }, label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth().background(Color.White), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().background(Color.White), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = password, onValueChange = { password = it }, label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().background(Color.White), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            // 👇 Rol selector
            Text("Rol", color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "user", onClick = { role = "user" })
                Text("Usuario", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(selected = role == "provider", onClick = { role = "provider" })
                Text("Proveedor", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.contains("@") && password.length >= 8) {
                        val fullName = "$nombre $apellido"
                        viewModel.registerUser(email, password, fullName, role, numerotel)
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
                text = "Already have an account? Login",
                textAlign = TextAlign.Center,
                color = Color(0xffFEFAE0),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginClick() }
            )
        }
    }
}
