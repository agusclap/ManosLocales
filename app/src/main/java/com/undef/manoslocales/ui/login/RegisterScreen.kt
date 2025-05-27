package com.undef.manoslocales.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.undef.manoslocales.R

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String) -> Unit = { _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numerotel by remember { mutableStateOf("") }
    val isFormValid = password.isNotBlank() && email.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3E2C1C)) // Fondo color 3E2C1C
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center).
                offset(y = (-40).dp), // Centrado de los elementos
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp).width(180.dp).offset(y = (-95).dp)
            )

            Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium,
                color = Color(0xffFEFAE0))
            Spacer(modifier = Modifier.height(24.dp))

            // Email input field
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Lastname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = numerotel,
                onValueChange = { numerotel = it },
                label = { Text("Phone Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Fondo blanco para el input
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username input field
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
                onClick = {
                    if(email.contains("@") && password.length >=8){
                    onRegisterClick(email, password)}
                          },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffFEFAE0), // Fondo blanco para el botón
                    contentColor = Color(0xff3E2C1C) // Texto negro en el botón
                )
            ) {
                Text(text = "Sign Up")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Link to login
            Text(
                text = "Already have an account? Login",
                textAlign = TextAlign.Center,
                color = Color(0xffFEFAE0), // Texto blanco para el enlace
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