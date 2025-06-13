// src/main/java/com/undef/manoslocales/ui/navigation/AppNavGraph.kt
package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // Use this specific viewModel import
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.undef.manoslocales.ui.database.AppDatabase // Import your AppDatabase
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.UserRepository // Import UserRepository
import com.undef.manoslocales.ui.database.UserViewModelFactory // Import your custom factory

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
import com.undef.manoslocales.ui.users.getUser

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // 1. Get the DAO (this is still on the main thread, but it just gets the DAO,
    //    not performs a query or creates the DB blocking the main thread)
    val userDao = remember { AppDatabase.getInstance(application).UserDao() }

    // 2. Create the Repository (takes the DAO)
    val userRepository = remember { UserRepository(userDao) }

    // 3. Create the UserViewModel using your custom factory
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(application, userRepository)
    )

    // And for FavoritosViewModel, assuming it's simple or handled elsewhere
    val favoritosViewModel: FavoritosViewModel = viewModel() // Or provide a factory if it needs params

    NavHost(navController = navController, startDestination = "login") {
        composable("register") {
            RegisterScreen(
                viewModel = userViewModel, // Use the new userViewModel
                onRegisterSuccess = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                // You might need to pass userViewModel here if LoginScreen needs it
                // viewModel = userViewModel, // Add if needed
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
            SettingScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(getUser(), navController = navController)
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