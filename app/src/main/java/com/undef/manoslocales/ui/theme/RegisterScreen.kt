package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3E2C1C)) // Fondo color 3E2C1C
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center), // Centrado de los elementos
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White // Texto blanco para el título
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Email input field
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username input field
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password input field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Register button
            Button(
                onClick = { onRegisterClick(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // Fondo blanco para el botón
                    contentColor = Color.Black // Texto negro en el botón
                )
            ) {
                Text(text = "Register")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Link to login
            Text(
                text = "Already have an account? Login",
                textAlign = TextAlign.Center,
                color = Color.White, // Texto blanco para el enlace
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}