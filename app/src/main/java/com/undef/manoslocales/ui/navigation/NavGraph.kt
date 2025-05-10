package com.undef.manoslocales.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.undef.manoslocales.ui.theme.EmprendedoresScreen
import com.undef.manoslocales.ui.theme.HomeScreen
import com.undef.manoslocales.ui.theme.LoginScreen
import com.undef.manoslocales.ui.theme.RegisterScreen
import com.undef.manoslocales.ui.theme.SettingScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("register") {
            RegisterScreen(
                onRegisterClick = { username, password ->
                    // Lógica de registro
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
                }
            )
        }
        composable("home") {
                HomeScreen(
                    navController = navController,  // Aquí pasas el navController a HomeScreen
                    onEmprendedoresClick = {
                        navController.navigate("emprendedores")  // Navegas a emprendedores
                    }
                )
            }
        composable("emprendedores") {
            EmprendedoresScreen(navController = navController)  // Pasamos navController
        }
        composable("settings") {
            SettingScreen(navController = navController)
        }
    }
}
