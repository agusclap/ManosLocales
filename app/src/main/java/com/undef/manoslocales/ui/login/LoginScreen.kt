package com.undef.manoslocales.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.Cafe
import com.undef.manoslocales.ui.theme.CafeOscuro
import com.undef.manoslocales.ui.theme.Crema

@OptIn(ExperimentalMaterial3Api::class)
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
    var isLoading by remember { mutableStateOf(false) }
    val isFormValid = password.isNotBlank() && email.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CafeOscuro)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.manoslocales),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = Crema,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = loginTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = loginTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Crema,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) { onForgotPasswordClick() },
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.loginUser(email, password)
            },
            enabled = isFormValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Crema,
                contentColor = Cafe
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Cafe, modifier = Modifier.size(24.dp))
            } else {
                Text("INGRESAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿No tenés cuenta? Registrate",
            color = Crema,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isLoading) { onRegisterClick() },
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // 🔁 Manejo de respuesta del login
    LaunchedEffect(viewModel.loginSuccess.value) {
        val success = viewModel.loginSuccess.value
        if (success == true) {
            viewModel.fetchUserInfo { user ->
                isLoading = false
                if (user != null) {
                    Toast.makeText(context, "¡Bienvenido, ${user.nombre}!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(user.role ?: "user")
                } else {
                    onLoginSuccess("user")
                }
            }
        } else if (success == false) {
            isLoading = false
            val error = viewModel.authErrorMessage.value ?: "Email o contraseña incorrectos"
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Crema,
    unfocusedTextColor = Crema,
    focusedBorderColor = Crema,
    unfocusedBorderColor = Crema.copy(alpha = 0.5f),
    focusedLabelColor = Crema,
    unfocusedLabelColor = Crema.copy(alpha = 0.7f),
    cursorColor = Crema,
    disabledTextColor = Crema.copy(alpha = 0.5f),
    disabledBorderColor = Crema.copy(alpha = 0.3f),
    disabledLabelColor = Crema.copy(alpha = 0.5f)
)
