package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavegarA: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inicio", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White // Fondo blanco como tu diseño
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp), // Margen general
            horizontalAlignment = Alignment.Start
        ) {
            // 1. SECCIÓN DE BIENVENIDA
            Text(
                text = "Bienvenido, Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937) // Gris oscuro elegante
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona un módulo para empezar.",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. GRILLA DE BOTONES (2 filas de 2 botones)

            // Fila 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    titulo = "Proveedores",
                    icono = Icons.Filled.Inventory, // Ícono de cajita
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegarA("proveedores") }
                )
                MenuCard(
                    titulo = "Categorías",
                    icono = Icons.Filled.Category,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegarA("categorias_lista") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    titulo = "Países",
                    icono = Icons.Filled.Public, // Ícono de mundo
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegarA("paises_lista") }
                )
                MenuCard(
                    titulo = "Ajustes",
                    icono = Icons.Filled.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Acción futura */ }
                )
            }
        }
    }
}

// COMPONENTE REUTILIZABLE: TARJETA DEL MENÚ
@Composable
fun MenuCard(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp), // Altura fija cuadrada
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp) // Bordes redondeados como tu imagen
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Usamos un Surface circular para el fondo del ícono (opcional, decorativo)
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF5E5E5E) // Gris oscuro para el ícono
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}