// src/main/java/com.undef.manoslocales.ui.navigation/AppNavGraph.kt
package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.database.AppDatabase
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.UserRepository
import com.undef.manoslocales.ui.database.UserViewModelFactory

import com.undef.manoslocales.ui.screens.FavoritosScreen
import com.undef.manoslocales.ui.login.ForgotPasswordScreen
import com.undef.manoslocales.ui.screens.HomeScreen
import com.undef.manoslocales.ui.login.LoginScreen
import com.undef.manoslocales.ui.producto.ProductosScreen
import com.undef.manoslocales.ui.screens.ProfileScreen
import com.undef.manoslocales.ui.proveedor.ProveedoresScreen
import com.undef.manoslocales.ui.login.RegisterScreen
import com.undef.manoslocales.ui.login.ResetLinkScreen
import com.undef.manoslocales.ui.screens.SettingScreen
import com.undef.manoslocales.ui.users.getUser // Considerar cómo getUser() obtiene el usuario ahora


// Asumiendo que FavoritosViewModel ya está definido


@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Instanciar SessionManager
    val sessionManager = remember { SessionManager(application) }

    // Instanciar dependencias de UserViewModel
    val userDao = remember { AppDatabase.getInstance(application).UserDao() }
    val userRepository = remember { UserRepository(userDao) }

    // Instanciar UserViewModel con las nuevas dependencias
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(application, userRepository, sessionManager) // <--- Pasa sessionManager
    )

    // Determinar la ruta de inicio basada en si el usuario ya está logueado
    val startDestination = if (userViewModel.isUserLoggedIn()) "home" else "login"

    val favoritosViewModel: FavoritosViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) { // <--- Usa la ruta de inicio dinámica
        composable("register") {
            RegisterScreen(
                viewModel = userViewModel,
                onRegisterSuccess = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = userViewModel,
                onLoginClick = { _, _ -> navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotpassword") }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onProductosClick = { navController.navigate("productos") },
                onProveedoresClick = { navController.navigate("proveedores") },
            )
        }
        composable("productos") {
            ProductosScreen(
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }
        composable("favoritos") {
            FavoritosScreen(
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }
        composable("proveedores") {
            ProveedoresScreen(
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }
        composable("settings") {
            SettingScreen(
                navController = navController,
                userViewModel = userViewModel // Pasa el userViewModel si necesitas cerrar sesión desde Settings
            )
        }
        composable("profile") {
            ProfileScreen(
                user = getUser(), // Aquí tendrías que obtener el usuario de sessionManager o de ViewModel
                navController = navController,
                userViewModel = userViewModel // Pasa el userViewModel si necesitas datos del usuario logueado
            )
        }
        composable("forgotpassword") {
            ForgotPasswordScreen(
                onSendResetClick = { navController.navigate("resetlink") },
                onBackToLoginClick = { navController.navigate("login") }
            )
        }
        composable("resetlink") {
            ResetLinkScreen(
                email = "example@mail.com",
                onBackToLoginClick = { navController.navigate("login") }
            )
        }
    }
}