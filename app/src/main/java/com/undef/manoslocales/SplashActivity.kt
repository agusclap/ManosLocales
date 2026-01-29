package com.undef.manoslocales

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManosLocalesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashRoute()
                }
            }
        }
    }
}

@Composable
private fun SplashRoute(viewModel: SplashViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startInitialLoad()
    }

    LaunchedEffect(uiState) {
        if (uiState is SplashViewModel.UiState.Success) {
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is Activity) {
                context.finish()
            }
        }
    }

    SplashScreen(
        uiState = uiState,
        onRetry = { viewModel.startInitialLoad() },
        onContinue = {
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is Activity) {
                context.finish()
            }
        }
    )
}

@Composable
private fun SplashScreen(
    uiState: SplashViewModel.UiState,
    onRetry: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.manoslocales),
            contentDescription = "Manos Locales",
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is SplashViewModel.UiState.Loading -> {
                Text(
                    text = "Cargando información inicial…",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            is SplashViewModel.UiState.Error -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(text = "Reintentar")
                }
                if (uiState.canContinue) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onContinue) {
                        Text(text = "Continuar")
                    }
                }
            }

            is SplashViewModel.UiState.Success -> {
                Text(
                    text = "¡Bienvenido!",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
