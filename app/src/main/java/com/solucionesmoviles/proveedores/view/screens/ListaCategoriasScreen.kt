package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    val listaCompleta by viewModel.listaCategoriasTodas.collectAsState()

    // 1. ESTADO DE BÚSQUEDA
    var textoBusqueda by remember { mutableStateOf("") }

    // 2. LÓGICA DE FILTRADO
    val categoriasFiltradas = listaCompleta.filter {
        it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                it.codigo.contains(textoBusqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Categorías", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = { BottomNavBar(itemSeleccionado = "categorias", onNavegar = onNavegar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCategoria,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva") }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {

            // 3. CAMPO DE TEXTO (BUSCADOR)
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar categoría...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. LISTA FILTRADA
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoriasFiltradas) { cat ->
                    val esEliminado = cat.estado == "*"
                    Card(
                        onClick = { if (!esEliminado) onEditarCategoria(cat.id) },
                        enabled = !esEliminado,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.alpha(if (esEliminado) 0.6f else 1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (esEliminado) Color.Gray else MaterialTheme.colorScheme.tertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(cat.nombre.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cat.nombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Cód: ${cat.codigo}", fontSize = 12.sp, color = Color.Gray)
                            }
                            EstadoChipCategoria(estado = cat.estado)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoChipCategoria(estado: String) {
    val (containerColor, contentColor, texto) = when (estado) {
        "A" -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Activo")
        "I" -> Triple(Color(0xFFFFEDD5), Color(0xFF9A3412), "Inactivo")
        "*" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Eliminado")
        else -> Triple(Color.Gray, Color.White, "Desc.")
    }
    Surface(color = containerColor, shape = RoundedCornerShape(50), modifier = Modifier.padding(start = 8.dp)) {
        Text(text = texto, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}