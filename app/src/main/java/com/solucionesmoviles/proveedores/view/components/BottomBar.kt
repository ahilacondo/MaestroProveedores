package com.solucionesmoviles.proveedores.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
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
    itemSeleccionado: String, // "inicio", "proveedores", "categorias", "paises"
    onNavegar: (String) -> Unit
) {
    NavigationBar(
        // CORRECCIÓN: Usamos el color dinámico del tema
        // En modo claro será Blanco. En modo oscuro será Gris Oscuro (SuperficieOscura).
        containerColor = MaterialTheme.colorScheme.surface,

        // Color de los íconos no seleccionados (Grisáceo en ambos modos)
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        // 1. INICIO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = itemSeleccionado == "inicio",
            onClick = { onNavegar("home") },
            colors = NavigationBarItemDefaults.colors(
                // Color cuando está seleccionado (Azul)
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                // Color de íconos/texto cuando NO está seleccionado (Dinámico)
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                // Color de íconos/texto cuando SI está seleccionado (Dinámico)
                selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                selectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        // 2. PROVEEDORES
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Proveedores") },
            label = { Text("Proveedores") },
            selected = itemSeleccionado == "proveedores",
            onClick = { onNavegar("proveedores") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                selectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        // 3. CATEGORÍAS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Category, contentDescription = "Categorías") },
            label = { Text("Categorías") },
            selected = itemSeleccionado == "categorias",
            onClick = { onNavegar("categorias_lista") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                selectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        // 4. PAÍSES
        NavigationBarItem(
            icon = { Icon(Icons.Default.Public, contentDescription = "Países") },
            label = { Text("Países") },
            selected = itemSeleccionado == "paises",
            onClick = { onNavegar("paises_lista") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                selectedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}