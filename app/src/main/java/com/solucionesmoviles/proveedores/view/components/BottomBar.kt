package com.solucionesmoviles.proveedores.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavBar(
    itemSeleccionado: String, // "inicio", "proveedores", "categorias", "paises"
    onNavegar: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        // 1. INICIO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = itemSeleccionado == "inicio",
            onClick = { onNavegar("home") }
        )
        // 2. PROVEEDORES
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Proveedores") },
            label = { Text("Proveedores") },
            selected = itemSeleccionado == "proveedores",
            onClick = { onNavegar("proveedores") }
        )
        // 3. CATEGORÍAS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Category, contentDescription = "Categorías") },
            label = { Text("Categorías") },
            selected = itemSeleccionado == "categorias",
            onClick = { onNavegar("categorias_lista") }
        )
        // 4. PAÍSES
        NavigationBarItem(
            icon = { Icon(Icons.Default.Public, contentDescription = "Países") },
            label = { Text("Países") },
            selected = itemSeleccionado == "paises",
            onClick = { onNavegar("paises_lista") }
        )
    }
}