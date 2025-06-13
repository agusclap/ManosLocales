package com.undef.manoslocales.ui.login

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel // Keep this import
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.UserViewModelFactory // Keep this import

@Composable
fun RegisterScreen(
    // Keep this parameter:
    viewModel: UserViewModel,
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numerotel by remember { mutableStateOf("") }
    val isFormValid = password.isNotBlank() && email.isNotBlank()
    val context = LocalContext.current

    // REMOVE this line:
    // val userViewModel: UserViewModel = viewModel(
    //     factory = UserViewModelFactory(context.applicationContext as Application)
    // )

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

            // Campos de entrada
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
                    .background(Color.White)
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
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (email.contains("@") && password.length >= 8) {
                        val user = User(
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            password = password,
                            phone = numerotel
                        )
                        // Use the 'viewModel' parameter directly
                        viewModel.userRegister(user) { success ->
                            if(success) {
                                onRegisterSuccess()
                                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Registration failed. Email might already be in use.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        if (!email.contains("@")) {
                            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                        } else if (password.length < 8) {
                            Toast.makeText(context, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
                        }
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
                Text(text = "Sign Up")
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // For preview, you'll need to provide a mock ViewModel or use a dummy one.
    // This is a basic example. In a real app, you might use a Hilt preview setup or similar.
// Provide a dummy ViewModel
}