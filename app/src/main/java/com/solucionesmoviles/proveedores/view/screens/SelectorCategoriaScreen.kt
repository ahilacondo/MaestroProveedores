package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SelectorCategoriaScreen(
    viewModel: ProveedorViewModel,
    onCategoriaSeleccionada: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    val categorias by viewModel.listaCategoriasActivas.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }

    val categoriasFiltradas = categorias.filter {
        it.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Categoría", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onCancelar) { Text("Cancelar", color = Color(0xFF2563EB)) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Buscar categoría...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray, focusedBorderColor = Color(0xFF2563EB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White, RoundedCornerShape(12.dp))) {
                items(categoriasFiltradas) { categoria ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onCategoriaSeleccionada(categoria.id) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = categoria.nombre, fontSize = 16.sp, color = Color.Black)
                        }
                        Divider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                    }
                }
            }
        }
    }
}