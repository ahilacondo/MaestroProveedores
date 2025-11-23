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
                    // COLOR DINÁMICO: Fondo de barra
                    containerColor = MaterialTheme.colorScheme.background,
                    // COLOR DINÁMICO: Texto de título
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        // COLOR DINÁMICO: Fondo de pantalla
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Bienvenido, Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                // COLOR DINÁMICO: Texto principal (Negro día / Blanco noche)
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona un módulo para empezar.",
                fontSize = 16.sp,
                color = Color.Gray // El gris funciona bien en ambos
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Fila 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    titulo = "Proveedores",
                    icono = Icons.Filled.Inventory,
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
                    icono = Icons.Filled.Public,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegarA("paises_lista") }
                )
                MenuCard(
                    titulo = "Ajustes",
                    icono = Icons.Filled.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavegarA("ajustes") }
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            // COLOR DINÁMICO: Tarjeta (Blanco en día, Gris Oscuro en noche)
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                // Ícono (Gris oscuro en día, Blanco en noche)
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                // Texto (Negro en día, Blanco en noche)
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}