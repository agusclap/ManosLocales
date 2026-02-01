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
import com.undef.manoslocales.ui.login.*
import com.undef.manoslocales.ui.notifications.FavoritesRepository
import com.undef.manoslocales.ui.notifications.FavoritosViewModelFactory
import com.undef.manoslocales.ui.notifications.NotificationViewModel
import com.undef.manoslocales.ui.producto.ProductoDetalleScreen
import com.undef.manoslocales.ui.producto.ProductosScreen
import com.undef.manoslocales.ui.proveedor.CreateProductScreen
import com.undef.manoslocales.ui.proveedor.EditProductScreen
import com.undef.manoslocales.ui.proveedor.MisProductosScreen
import com.undef.manoslocales.ui.proveedor.NearbyProvidersScreen
import com.undef.manoslocales.ui.proveedor.ProveedorDetalleScreen
import com.undef.manoslocales.ui.proveedor.ProveedoresScreen
import com.undef.manoslocales.ui.screens.*
import com.undef.manoslocales.ui.splash.SplashScreen

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
    val notificationViewModel: NotificationViewModel = viewModel()

    LaunchedEffect(key1 = sessionManager.isLoggedIn()) {
        if (sessionManager.isLoggedIn()) {
            favoritosViewModel.loadFavoritesForCurrentUser()
            notificationViewModel.startListening()
        } else {
            favoritosViewModel.clearFavorites()
            notificationViewModel.clearNotifications()
        }
    }

    val startDestination = "splash"


    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(navController, sessionManager)
        }

        composable("register") {
            RegisterScreen(
                viewModel = userViewModel,
                onRegisterSuccess = { email, uid -> 
                    navController.navigate("verification/$email/$uid") 
                },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("verification/{email}/{uid}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""
            val uid = backStack.arguments?.getString("uid") ?: ""
            VerificationScreen(
                email = email,
                uid = uid,
                viewModel = userViewModel,
                onVerificationSuccess = { navController.navigate("login") },
                onBack = { navController.popBackStack() }
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
                notificationViewModel = notificationViewModel,
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
            ProveedorDetalleScreen(
                providerId = providerId, 
                viewModel = userViewModel, 
                onBack = { navController.popBackStack() },
                onProviderClick = { newId ->
                    navController.navigate("proveedorDetalle/$newId") {
                        popUpTo("proveedorDetalle/$providerId") { inclusive = true }
                    }
                }
            )
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
                viewModel = userViewModel,
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
                userViewModel = userViewModel,
                onBackToLoginClick = { navController.navigate("login") },
                onCodeSent = { email -> navController.navigate("reset_verification/$email") }
            )
        }

        composable("reset_verification/{email}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""
            ResetVerificationScreen(
                email = email,
                viewModel = userViewModel,
                onCodeVerified = { code -> navController.navigate("new_password/$email/$code") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("new_password/{email}/{code}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""
            val code = backStack.arguments?.getString("code") ?: ""
            NewPasswordScreen(
                email = email,
                code = code,
                viewModel = userViewModel,
                onSuccess = { navController.navigate("login") }
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
            CreateProductScreen(viewModel = userViewModel, navController = navController)
        }
    }
}
