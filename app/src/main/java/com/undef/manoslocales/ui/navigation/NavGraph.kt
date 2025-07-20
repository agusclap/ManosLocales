// src/main/java/com.undef.manoslocales.ui.navigation/AppNavGraph.kt
package com.undef.manoslocales.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.undef.manoslocales.ui.notifications.FavoritesRepository
import com.undef.manoslocales.ui.notifications.FavoritosViewModelFactory
import com.undef.manoslocales.ui.producto.ProductoDetalleScreen
import com.undef.manoslocales.ui.producto.ProductosScreen
import com.undef.manoslocales.ui.proveedor.CreateProductScreen
import com.undef.manoslocales.ui.proveedor.EditProductScreen
import com.undef.manoslocales.ui.proveedor.MisProductosScreen
import com.undef.manoslocales.ui.proveedor.NearbyProvidersScreen
import com.undef.manoslocales.ui.proveedor.ProveedorDetalleScreen
import com.undef.manoslocales.ui.proveedor.ProveedoresScreen
import com.undef.manoslocales.ui.screens.ChangePasswordScreen
import com.undef.manoslocales.ui.screens.EditProfileScreen
import com.undef.manoslocales.ui.screens.FavoritosScreen
import com.undef.manoslocales.ui.screens.HomeScreen
import com.undef.manoslocales.ui.screens.ProfileScreen
import com.undef.manoslocales.ui.screens.SettingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val sessionManager = remember { SessionManager(application) }
    val favoritesRepository = remember { FavoritesRepository() }

    val favoritosViewModelFactory = remember {
        FavoritosViewModelFactory(application, favoritesRepository, sessionManager)
    }

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(application, sessionManager)
    )
    val favoritosViewModel: FavoritosViewModel = viewModel(factory = favoritosViewModelFactory)

    LaunchedEffect(key1 = sessionManager.isLoggedIn()) {
        if (sessionManager.isLoggedIn()) {
            favoritosViewModel.loadFavoritesForCurrentUser()
        } else {
            favoritosViewModel.clearFavorites()
        }
    }

    val startDestination = if (sessionManager.isLoggedIn()) "home" else "login"


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
                viewModel = userViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }
        composable("favoritos") {
            FavoritosScreen(
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable("changePassword") {
            ChangePasswordScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("productoDetalle/{productId}/{providerId}") { backStack ->
            val pid = backStack.arguments?.getString("productId") ?: ""
            val provId = backStack.arguments?.getString("providerId") ?: ""
            ProductoDetalleScreen(pid, provId, viewModel = userViewModel, navController = navController)
        }

        composable("proveedorDetalle/{providerId}") { backStack ->
            val providerId = backStack.arguments?.getString("providerId") ?: ""
            ProveedorDetalleScreen(providerId = providerId, viewModel = userViewModel, onBack = {
                navController.popBackStack()
            })
        }

        composable("editProfile") {
            EditProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable("nearby") {
            NearbyProvidersScreen(viewModel = userViewModel)
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
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("forgotpassword") {
            ForgotPasswordScreen(
                onBackToLoginClick = { navController.navigate("login") }
            )
        }

        composable("resetlink") {
            ResetLinkScreen(
                email = "example@mail.com",
                onBackToLoginClick = { navController.navigate("login") }
            )
        }

        composable("misproductos") {
            MisProductosScreen(navController, userViewModel)
        }

        composable("editarProducto/{productId}") { backStackEntry ->
            val prodId = backStackEntry.arguments?.getString("productId") ?: return@composable
            EditProductScreen(productId = prodId, viewModel = userViewModel, navController = navController)
        }

        composable("createproduct") {
            CreateProductScreen(viewModel = userViewModel)
        }
    }
}
