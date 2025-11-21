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
    // 1. Observamos los datos del ViewModel (Lista filtrada y ordenada)
    val proveedores by viewModel.listaProveedores.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.searchQuery.collectAsState()

    // Estado para saber si estamos ordenando por Nombre (para pintar el ícono)
    val ordenadoPorNombre by viewModel.ordenarPorNombre.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Maestro de Proveedores", fontWeight = FontWeight.Bold) },
                actions = {
                    // BOTÓN DE ORDENAMIENTO (Opcional de la guía)
                    IconButton(onClick = { viewModel.cambiarOrden() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Cambiar Orden",
                            // Azul si es A-Z, Gris si es por RUC (Feedback visual)
                            tint = if (ordenadoPorNombre) Color(0xFF2563EB) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                itemSeleccionado = "proveedores", // Marcamos la pestaña actual
                onNavegar = onNavegar
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoProveedor,
                containerColor = Color(0xFF2563EB), // Azul Institucional
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proveedor")
            }
        },
        containerColor = Color(0xFFF3F4F6) // Fondo Gris Claro
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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Buscar por nombre o RUC...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF2563EB)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // A. AVATAR CON INICIAL
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBEAFE)), // Fondo Azul Claro
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (proveedor.nombre.isNotEmpty()) proveedor.nombre.take(1).uppercase() else "?",
                    color = Color(0xFF1E40AF), // Texto Azul Oscuro
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
                    color = Color.Black
                )
                Text(
                    text = "RUC: ${proveedor.ruc}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                // Mostramos el tipo pequeño (ej: Nacional)
                Text(
                    text = proveedor.tipoProveedor,
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB)
                )
            }

            // C. CHIP DE ESTADO
            EstadoChip(activo = proveedor.estado == "A")
        }
    }
}

@Composable
fun EstadoChip(activo: Boolean) {
    // Colores dinámicos según estado
    val containerColor = if (activo) Color(0xFFDCFCE7) else Color(0xFFFEE2E2) // Verde Claro / Rojo Claro
    val contentColor = if (activo) Color(0xFF166534) else Color(0xFF991B1B)   // Verde Oscuro / Rojo Oscuro
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