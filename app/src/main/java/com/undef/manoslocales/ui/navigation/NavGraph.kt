package com.undef.manoslocales.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.undef.manoslocales.ui.screens.EmprendedoresScreen
import com.undef.manoslocales.ui.screens.ForgotPasswordScreen
import com.undef.manoslocales.ui.screens.HomeScreen
import com.undef.manoslocales.ui.screens.LoginScreen
import com.undef.manoslocales.ui.screens.ProfileScreen
import com.undef.manoslocales.ui.screens.ProveedoresScreen
import com.undef.manoslocales.ui.screens.RegisterScreen
import com.undef.manoslocales.ui.screens.ResetLinkScreen
import com.undef.manoslocales.ui.screens.SettingScreen
import com.undef.manoslocales.ui.users.getUser

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("register") {
            RegisterScreen(
                onRegisterClick = { username, password ->
                    navController.navigate("login")
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    // Lógica del login, si es exitosa:
                    navController.navigate("home")
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onForgotPasswordClick = {
                    navController.navigate("forgotpassword")
                }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onEmprendedoresClick = {
                    navController.navigate("emprendedores")
                },
                onProveedoresClick = {
                    navController.navigate("proveedores")
                }
            )
        }
        composable("emprendedores") {
            EmprendedoresScreen(navController = navController)  // Pasamos navController
        }
        composable("proveedores") {
            ProveedoresScreen(navController = navController)
        }
        composable("settings") {
            SettingScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(getUser(), navController = navController)
        }
        composable("forgotpassword") {
            ForgotPasswordScreen(
                onSendResetClick = { email ->
                    // TODO LOGICA PARA ENVIAR MAIL
                },
                onBackToLoginClick = {
                    navController.navigate("login")
                }
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
