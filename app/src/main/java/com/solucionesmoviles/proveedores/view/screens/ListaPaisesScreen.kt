package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.solucionesmoviles.proveedores.model.Pais
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPaisesScreen(
    viewModel: ProveedorViewModel,
    onNuevoPais: () -> Unit,
    onEditarPais: (Int) -> Unit,
    onNavegar: (String) -> Unit
) {
    // Observamos la lista completa (Todos los estados, para mantenimiento)
    val paises by viewModel.listaPaisesTodos.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mantenimiento Países", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavBar(itemSeleccionado = "paises", onNavegar = onNavegar)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoPais,
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo") }
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paises) { pais ->
                Card(
                    onClick = { onEditarPais(pais.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(pais.codigo.take(2), fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pais.nombre, fontWeight = FontWeight.Bold)
                            Text("Cód: ${pais.codigo}", fontSize = 12.sp, color = Color.Gray)
                        }
                        EstadoChip(activo = pais.estado == "A")
                    }
                }
            }
        }
    }
}