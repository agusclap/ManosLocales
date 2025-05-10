package com.undef.manoslocales.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.undef.manoslocales.ui.theme.LoginScreen
import com.undef.manoslocales.ui.theme.RegisterScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            RegisterScreen(
                onRegisterClick = { username, password ->
                    // Aca tenemos que poner la logica del registro
                },
                onLoginClick = { //Hace referencia a el texto "Already have an account..." porque ahi defini el onLoginClick
                    navController.navigate("login")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    // Logica del login RODILLIN
                },
                onRegisterClick = { //Hace referencia a el texto "Dont have an account..." porque ahi defini el onRegisterClick
                    navController.navigate("register")
                }
            )
        }
    }
}
