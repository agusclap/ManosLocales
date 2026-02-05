package com.undef.manoslocales.ui.login

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

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onLoginSuccess: (role: String) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val sessionManager = remember { SessionManager(application) }
    
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(application, sessionManager)
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = userViewModel,
                onLoginSuccess = onLoginSuccess,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotpassword") }
            )
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
    }
}
