package com.undef.manoslocales.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Crema,
    secondary = CafeClaro,
    tertiary = CremaOscuro,
    background = CafeOscuro,
    surface = Cafe,
    onPrimary = Cafe,
    onSecondary = Crema,
    onTertiary = Cafe,
    onBackground = Crema,
    onSurface = Crema,
    surfaceVariant = CafeClaro,
    onSurfaceVariant = CremaOscuro
)

private val LightColorScheme = lightColorScheme(
    primary = Cafe,
    secondary = CafeClaro,
    tertiary = CremaOscuro,
    background = Crema,
    surface = Crema,
    onPrimary = Crema,
    onSecondary = Crema,
    onTertiary = Cafe,
    onBackground = Cafe,
    onSurface = Cafe,
    surfaceVariant = CremaOscuro,
    onSurfaceVariant = Cafe
)

@Composable
fun ManosLocalesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color deshabilitado para mantener la paleta personalizada
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}