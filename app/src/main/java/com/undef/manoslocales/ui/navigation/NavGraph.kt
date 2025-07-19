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
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.UserViewModelFactory
import com.undef.manoslocales.ui.login.ForgotPasswordScreen
import com.undef.manoslocales.ui.login.LoginScreen
import com.undef.manoslocales.ui.login.RegisterScreen
import com.undef.manoslocales.ui.login.ResetLinkScreen
import com.undef.manoslocales.ui.producto.ProductosScreen
import com.undef.manoslocales.ui.proveedor.CreateProductScreen
import com.undef.manoslocales.ui.proveedor.ProveedoresScreen
import com.undef.manoslocales.ui.screens.FavoritosScreen
import com.undef.manoslocales.ui.screens.HomeScreen
import com.undef.manoslocales.ui.screens.ProfileScreen
import com.undef.manoslocales.ui.screens.SettingScreen
import com.undef.manoslocales.ui.users.getUser

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val sessionManager = remember { SessionManager(application) }

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(application, sessionManager)
    )

    val startDestination = if (userViewModel.isUserLoggedIn()) "home" else "login"

    val favoritosViewModel: FavoritosViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
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
                onLoginSuccess = { role ->
                    when (role) {
                        "provider" -> navController.navigate("home")
                        "user" -> navController.navigate("home")
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotpassword") }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                userViewModel = userViewModel,
                onProductosClick = { navController.navigate("productos") },
                onProveedoresClick = { navController.navigate("proveedores") },
                onCreateProductClick = { navController.navigate("createproduct") }
            )
        }
        composable("productos") {
            ProductosScreen(
                navController = navController,
                viewModel = userViewModel
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
                userViewModel = userViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                user = getUser(), // Considerar usar sessionManager.getLoggedInUserEmail()
                navController = navController,
                userViewModel = userViewModel
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
        composable("createproduct") {
            CreateProductScreen(viewModel = userViewModel)
        }
    }
}
