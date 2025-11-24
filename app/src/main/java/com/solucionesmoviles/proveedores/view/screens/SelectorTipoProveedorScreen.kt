package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorTipoProveedorScreen(
    viewModel: ProveedorViewModel,
    onTipoSeleccionado: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    // Usamos la lista de activos (A)
    val tipos by viewModel.listaTiposActivos.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }

    val tiposFiltrados = tipos.filter {
        it.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seleccionar Tipo", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar tipo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(tiposFiltrados) { tipo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTipoSeleccionado(tipo.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tipo.nombre, fontSize = 16.sp)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}