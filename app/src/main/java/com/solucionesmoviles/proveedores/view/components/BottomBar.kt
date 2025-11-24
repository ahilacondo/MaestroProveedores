package com.solucionesmoviles.proveedores.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    itemSeleccionado: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        // 1. INICIO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = itemSeleccionado == "inicio",
            onClick = { onNavegar("home") },
            // Esto oculta el texto si no está seleccionado (limpia la vista)
            alwaysShowLabel = false,
            colors = obtenerColoresNav()
        )
        // 2. PROVEEDORES (Nombre corto "Prov.")
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Proveedores") },
            label = { Text("Prov.") },
            selected = itemSeleccionado == "proveedores",
            onClick = { onNavegar("proveedores") },
            alwaysShowLabel = false,
            colors = obtenerColoresNav()
        )
        // 3. CATEGORÍAS (Nombre corto "Categ.")
        NavigationBarItem(
            icon = { Icon(Icons.Default.Category, contentDescription = "Categorías") },
            label = { Text("Categ.") },
            selected = itemSeleccionado == "categorias",
            onClick = { onNavegar("categorias_lista") },
            alwaysShowLabel = false,
            colors = obtenerColoresNav()
        )
        // 4. PAÍSES
        NavigationBarItem(
            icon = { Icon(Icons.Default.Public, contentDescription = "Países") },
            label = { Text("Países") },
            selected = itemSeleccionado == "paises",
            onClick = { onNavegar("paises_lista") },
            alwaysShowLabel = false,
            colors = obtenerColoresNav()
        )
        // 5. TIPOS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Class, contentDescription = "Tipos") },
            label = { Text("Tipos") },
            selected = itemSeleccionado == "tipos",
            onClick = { onNavegar("tipos_lista") },
            alwaysShowLabel = false,
            colors = obtenerColoresNav()
        )
    }
}

@Composable
fun obtenerColoresNav() = NavigationBarItemDefaults.colors(
    indicatorColor = MaterialTheme.colorScheme.tertiary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedIconColor = MaterialTheme.colorScheme.onTertiary,
    selectedTextColor = MaterialTheme.colorScheme.onSurface
)