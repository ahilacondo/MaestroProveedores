package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
                title = { Text("Proveedores", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.cambiarOrden() }) {
                        if (ordenadoPorNombre) {
                            Icon(Icons.Default.SortByAlpha, contentDescription = "A-Z", tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Icon(Icons.Default.Numbers, contentDescription = "RUC", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = { BottomNavBar(itemSeleccionado = "proveedores", onNavegar = onNavegar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoProveedor,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo") }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre o RUC...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Text(
                text = if (ordenadoPorNombre) "Ordenado por: Nombre (A-Z)" else "Ordenado por: RUC",
                fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (proveedores.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron proveedores", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(proveedores) { proveedor ->
                        ProveedorItem(
                            proveedor = proveedor,
                            // REGLA: Si está eliminado (*), NO HACEMOS NADA al hacer click.
                            onClick = {
                                if (proveedor.estado != "*") {
                                    onEditarProveedor(proveedor.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProveedorItem(proveedor: Proveedor, onClick: () -> Unit) {
    val esEliminado = proveedor.estado == "*"

    Card(
        onClick = onClick,
        // Deshabilitamos el efecto visual de click si está eliminado
        enabled = !esEliminado,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        // Opacidad reducida si está eliminado para que parezca "fantasma"
        modifier = Modifier.alpha(if (esEliminado) 0.6f else 1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (esEliminado) Color.Gray else MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (proveedor.nombre.isNotEmpty()) proveedor.nombre.take(1).uppercase() else "?",
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = proveedor.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "RUC: ${proveedor.ruc}", fontSize = 14.sp, color = Color.Gray)
                Text(text = proveedor.tipoProveedor, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }

            // Pasamos el estado (letra) en lugar de booleano
            EstadoChip(estado = proveedor.estado)
        }
    }
}

@Composable
fun EstadoChip(estado: String) {
    // LÓGICA DE COLORES SOLICITADA:
    // A (Activo) -> Verde
    // I (Inactivo) -> Naranja
    // * (Eliminado) -> Rojo
    val (containerColor, contentColor, texto) = when (estado) {
        "A" -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Activo")
        "I" -> Triple(Color(0xFFFFEDD5), Color(0xFF9A3412), "Inactivo") // Naranja
        "*" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Eliminado") // Rojo
        else -> Triple(Color.Gray, Color.White, "Desc.")
    }

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