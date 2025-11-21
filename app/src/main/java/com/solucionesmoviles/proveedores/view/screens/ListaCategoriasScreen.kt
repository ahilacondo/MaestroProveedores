package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCategoriasScreen(
    viewModel: ProveedorViewModel,
    onNuevaCategoria: () -> Unit,
    onEditarCategoria: (Int) -> Unit,
    onNavegar: (String) -> Unit
) {
    val categorias by viewModel.listaCategoriasTodas.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mantenimiento Categorías", fontWeight = FontWeight.Bold) })
        },
        bottomBar = { BottomNavBar(itemSeleccionado = "categorias", onNavegar = onNavegar) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaCategoria, containerColor = Color(0xFF2563EB), contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Nueva")
            }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias) { cat ->
                Card(
                    onClick = { onEditarCategoria(cat.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat.nombre.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF3730A3))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat.nombre, fontWeight = FontWeight.Bold)
                            Text("Cód: ${cat.codigo}", fontSize = 12.sp, color = Color.Gray)
                        }
                        EstadoChip(activo = cat.estado == "A")
                    }
                }
            }
        }
    }
}