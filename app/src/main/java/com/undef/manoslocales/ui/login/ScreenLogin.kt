package com.undef.manoslocales.ui.login

import android.app.Application // Necesario para la preview
import android.widget.Toast // Necesario para la vista real
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext // Import for Toast
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.AppDatabase // Import para el preview
import com.undef.manoslocales.ui.database.UserRepository // Import para el preview
import com.undef.manoslocales.ui.database.UserViewModel // Import UserViewModel
import com.undef.manoslocales.ui.database.UserViewModelFactory // Import para el preview


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: UserViewModel // ViewModel passed as a parameter
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isFormValid = password.isNotBlank() && email.isNotBlank()
    val context = LocalContext.current // Get context for Toast messages

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
            text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium,
            color = Color(0xffFEFAE0)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // TextField para el usuario
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TextField para la contraseña
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
                // Now, call the loginUser method from your ViewModel
                viewModel.loginUser(email, password) { user ->
                    if (user != null) {
                        Toast.makeText(context, "Login Exitoso!", Toast.LENGTH_SHORT).show()
                        // Aquí, onLoginClick se encarga de la navegación
                        onLoginClick(email, password)
                    } else {
                        Toast.makeText(context, "Email o Contraseña inválidos", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xffFEFAE0),
                contentColor = Color(0xff3E2C1C)
            )
        ) {
            Text(text = "Log In")
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
}

@Composable
fun MiImage(painter: Painter) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // FIX: Provide a dummy UserViewModel instance for the preview
    // This creates a minimal setup for the ViewModel to exist in the preview context.
    LoginScreen(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        onForgotPasswordClick = {},
        viewModel = UserViewModel( // marca como error pero no pasa nada ejecuta igual
            Application(), // Dummy Application context for preview
            // For preview, you need a dummy UserRepository that might not even do real DB operations.
            // Or, if your AppDatabase.getInstance() is light enough to run in preview, use it.
            // If the AppDatabase.getInstance() in preview causes issues, you'd create a mock UserRepository.
            UserRepository(AppDatabase.getInstance(LocalContext.current).UserDao())
        )
    )
}