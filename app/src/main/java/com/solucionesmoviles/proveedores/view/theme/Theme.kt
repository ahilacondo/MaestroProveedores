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

// Definimos tu paleta de colores CLARA (Light)
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

// La oscura la dejamos igual a la clara para "forzar" el diseño,
// o usamos una por defecto, pero lo controlaremos abajo.
private val DarkColorScheme = LightColorScheme

@Composable
fun MaestroProveedoresTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Desactivamos colores dinámicos para respetar tu marca
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

    // Esto pinta la barra de estado (donde está la hora y batería) del color de tu app
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pintamos la barra de estado de Blanco o Azul según prefieras
            window.statusBarColor = Color.White.toArgb()
            // Hacemos que los íconos de la barra (batería, hora) sean oscuros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}