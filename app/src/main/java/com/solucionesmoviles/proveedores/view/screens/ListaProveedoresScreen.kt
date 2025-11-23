package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.proveedores.model.Proveedor
import com.solucionesmoviles.proveedores.view.components.BottomNavBar
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProveedoresScreen(
    viewModel: ProveedorViewModel,
    onNuevoProveedor: () -> Unit,
    onEditarProveedor: (Int) -> Unit,
    onVolver: () -> Unit,
    onNavegar: (String) -> Unit
) {
    val proveedores by viewModel.listaProveedores.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.searchQuery.collectAsState()
    val ordenadoPorNombre by viewModel.ordenarPorNombre.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Maestro de Proveedores", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.cambiarOrden() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Cambiar Orden",
                            tint = if (ordenadoPorNombre) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    // COLOR DINÁMICO: Fondo de barra
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                itemSeleccionado = "proveedores",
                onNavegar = onNavegar
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoProveedor,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proveedor")
            }
        },
        // COLOR DINÁMICO: Fondo de pantalla
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 2. BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre o RUC...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Fondos dinámicos para el input
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. LISTA DE RESULTADOS
            if (proveedores.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron proveedores", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(proveedores) { proveedor ->
                        ProveedorItem(
                            proveedor = proveedor,
                            onClick = { onEditarProveedor(proveedor.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProveedorItem(proveedor: Proveedor, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            // COLOR DINÁMICO: Tarjeta (Blanca en día / Gris en noche)
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A. AVATAR
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary), // Azul suave
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (proveedor.nombre.isNotEmpty()) proveedor.nombre.take(1).uppercase() else "?",
                    color = MaterialTheme.colorScheme.onTertiary, // Texto sobre el azul suave
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // B. INFO TEXTO
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = proveedor.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    // COLOR DINÁMICO: Texto principal
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "RUC: ${proveedor.ruc}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = proveedor.tipoProveedor,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // C. CHIP DE ESTADO
            EstadoChip(activo = proveedor.estado == "A")
        }
    }
}

@Composable
fun EstadoChip(activo: Boolean) {
    val containerColor = if (activo) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
    val contentColor = if (activo) Color(0xFF166534) else Color(0xFF991B1B)
    val texto = if (activo) "Activo" else "Inactivo"

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = texto,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}