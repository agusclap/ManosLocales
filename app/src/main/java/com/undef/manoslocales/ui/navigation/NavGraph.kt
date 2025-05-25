package com.undef.manoslocales.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.undef.manoslocales.ui.screens.FavoritosScreen
import com.undef.manoslocales.ui.screens.ForgotPasswordScreen
import com.undef.manoslocales.ui.screens.HomeScreen
import com.undef.manoslocales.ui.screens.LoginScreen
import com.undef.manoslocales.ui.screens.ProductosScreen
import com.undef.manoslocales.ui.screens.ProfileScreen
import com.undef.manoslocales.ui.screens.ProveedoresScreen
import com.undef.manoslocales.ui.screens.RegisterScreen
import com.undef.manoslocales.ui.screens.ResetLinkScreen
import com.undef.manoslocales.ui.screens.SettingScreen
import com.undef.manoslocales.ui.users.getUser

@Composable
fun AppNavGraph(navController: NavHostController) {
    val favoritosViewModel = remember { FavoritosViewModel() }

    NavHost(navController = navController, startDestination = "login") {
        composable("register") {
            RegisterScreen(
                onRegisterClick = { _, _ -> navController.navigate("login") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = { _, _ -> navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotpassword") }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onProductosClick = { navController.navigate("productos") },
                onProveedoresClick = { navController.navigate("proveedores") }
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
            SettingScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(getUser(), navController = navController)
        }
        composable("forgotpassword") {
            ForgotPasswordScreen(
                onSendResetClick = { /* TODO */ },
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
