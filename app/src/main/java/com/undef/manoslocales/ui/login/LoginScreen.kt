// Assuming this is your LoginScreen.kt (or ScreenLogin.kt if you renamed it)
package com.undef.manoslocales.ui.login

import android.app.Application
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.AppDatabase
import com.undef.manoslocales.ui.database.UserRepository
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.data.SessionManager // <--- ADD THIS IMPORT


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: UserViewModel
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
                viewModel.loginUser(email, password) { user ->
                    if (user != null) {
                        Toast.makeText(context, "Login Exitoso!", Toast.LENGTH_SHORT).show()
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
    // Get a dummy context for the preview
    val context = LocalContext.current
    val application = Application() // Or use an actual Mockito mock of Application if preferred for more complex scenarios

    // Instantiate dummy dependencies for the ViewModel in the preview
    // Note: AppDatabase.getInstance() for preview should ideally be a mock or a in-memory database.
    // For now, if AppDatabase.getInstance(context) works in preview, it's fine.
    // If it causes issues, you'd create a mock UserDao and UserRepository here.
    val dummyUserDao = AppDatabase.getInstance(context).UserDao()
    val dummyUserRepository = UserRepository(dummyUserDao)
    val dummySessionManager = SessionManager(context) // <--- INSTANTIATE SessionManager HERE

    LoginScreen(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        onForgotPasswordClick = {},
        // Pass the fully instantiated dummy ViewModel
        viewModel = UserViewModel(
            application,
            dummyUserRepository,
            dummySessionManager // <--- PASS THE SESSION MANAGER TO THE VIEWMODEL
        )
    )
}