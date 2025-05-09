package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.manoslocales.R

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff3E2C1C))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.manoslocales),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(180.dp).width(180.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium,
            color = Color(0xffFEFAE0))
        Spacer(modifier = Modifier.height(24.dp))

        // TextField para el usuario
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TextField para la contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Olvidaste tu contraseña?",
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onLoginClick(username, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xffFEFAE0),
                contentColor = Color(0xff3E2C1C)

            )
        ) {
            Text(text = "Log In")
        }
    }
}

@Composable
fun MiImage(painter: Painter) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen { _, _ -> }
}