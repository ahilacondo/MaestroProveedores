package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.proveedores.model.Categoria
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCategoriaScreen(
    viewModel: ProveedorViewModel,
    idCategoria: Int,
    onGuardarFinalizado: () -> Unit,
    onCancelar: () -> Unit
) {
    var codigoActual by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var catActual by remember { mutableStateOf<Categoria?>(null) }
    val esEdicion = idCategoria != 0

    LaunchedEffect(idCategoria) {
        if (esEdicion) {
            val c = viewModel.getCategoriaById(idCategoria)
            c?.let {
                catActual = it
                codigoActual = it.codigo
                nombre = it.nombre
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar Categoría" else "Crear Categoría", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
                actions = {
                    TextButton(onClick = {
                        if (nombre.isNotBlank()) {
                            viewModel.guardarCategoria(
                                Categoria(
                                    id = if (esEdicion) idCategoria else 0,
                                    // Código vacío si es nuevo -> ViewModel lo genera
                                    codigo = if (esEdicion) codigoActual else "",
                                    nombre = nombre,
                                    estado = catActual?.estado ?: "A"
                                )
                            )
                            onGuardarFinalizado()
                        }
                    }) { Text("Guardar", fontWeight = FontWeight.Bold) }
                }
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (esEdicion) {
                        Text(text = "Código: $codigoActual", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de Categoría") },
                        placeholder = { Text("Ej: Lácteos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            if (esEdicion && catActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val estado = catActual!!.estado

                Button(
                    onClick = {
                        if (estado == "A") viewModel.inactivarCategoria(catActual!!) else viewModel.reactivarCategoria(catActual!!)
                        onGuardarFinalizado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7), contentColor = Color(0xFFD97706)),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (estado == "A") "Inactivar" else "Reactivar") }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.eliminarCategoria(catActual!!); onGuardarFinalizado() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Eliminar") }
            }
        }
    }
}