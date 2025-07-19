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
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: (role: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginRequested by remember { mutableStateOf(false) }
    val isFormValid = password.isNotBlank() && email.isNotBlank()

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
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .width(180.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xffFEFAE0)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot your password?",
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Right,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                viewModel.loginUser(email, password)
                loginRequested = true
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xffFEFAE0),
                contentColor = Color(0xff3E2C1C)
            )
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "Don't have an account? Register",
            color = Color(0xffFEFAE0),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRegisterClick() }
        )
    }

    // 🔁 Efecto para manejar la respuesta del login
    if (loginRequested) {
        LaunchedEffect(viewModel.loginSuccess.value) {
            if (viewModel.loginSuccess.value == true) {
                viewModel.getUserRole { role ->
                    if (role != null) {
                        Toast.makeText(context, "Bienvenido ($role)", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(role)
                    } else {
                        Toast.makeText(context, "No se pudo obtener el rol", Toast.LENGTH_SHORT).show()
                    }
                    loginRequested = false
                }
            } else if (viewModel.loginSuccess.value == false) {
                Toast.makeText(context, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                loginRequested = false
            }
        }
    }
}
