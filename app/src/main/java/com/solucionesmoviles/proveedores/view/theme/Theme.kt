package com.solucionesmoviles.proveedores.view.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta CLARA
private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulSecundario,
    tertiary = AzulFondo,
    background = GrisFondo,
    surface = Blanco,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = AzulSecundario,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Paleta OSCURA
private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimario,
    secondary = AzulSecundario,
    tertiary = AzulFondo,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = AzulSecundario,
    onBackground = TextoBlanco, // Texto blanco en fondo negro
    onSurface = TextoBlanco     // Texto blanco en tarjeta gris
)

@Composable
fun MaestroProveedoresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Íconos blancos en modo oscuro, negros en modo claro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}