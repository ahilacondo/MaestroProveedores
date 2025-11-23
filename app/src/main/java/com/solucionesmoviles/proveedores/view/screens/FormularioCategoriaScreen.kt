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
import kotlinx.coroutines.launch

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

    // Estados para alertas y validaciones
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoInactivar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // LÓGICA DE BLOQUEO
    val esEliminado = catActual?.estado == "*"
    val habilitado = !esEliminado

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar Categoría" else "Crear Categoría", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) } },
                actions = {
                    if (habilitado) {
                        TextButton(onClick = {
                            if (nombre.isNotBlank()) {
                                viewModel.guardarCategoria(
                                    Categoria(
                                        id = if (esEdicion) idCategoria else 0,
                                        codigo = if (esEdicion) codigoActual else "",
                                        nombre = nombre,
                                        estado = catActual?.estado ?: "A"
                                    )
                                )
                                onGuardarFinalizado()
                            }
                        }) { Text("Guardar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (esEdicion) {
                        Text(text = "Código: $codigoActual", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    CampoTextoSimple(
                        label = "Nombre",
                        valor = nombre,
                        placeholder = "Ej: Lácteos",
                        enabled = habilitado, // <--- BLOQUEO
                        onChange = { nombre = it }
                    )
                }
            }

            if (esEdicion && catActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val estado = catActual!!.estado

                // BOTÓN REACTIVAR / INACTIVAR
                Button(
                    onClick = {
                        if (estado == "A") {
                            // REGLA 2: Validar integridad
                            scope.launch {
                                if (viewModel.puedeInactivarCategoria(idCategoria)) {
                                    mostrarDialogoInactivar = true
                                } else {
                                    snackbarHostState.showSnackbar("Error: Hay proveedores activos usando esta categoría.")
                                }
                            }
                        } else {
                            viewModel.reactivarCategoria(catActual!!)
                            onGuardarFinalizado()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (estado == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                        contentColor = if (estado == "A") Color(0xFFD97706) else Color(0xFF166534)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(if (estado == "A") "Inactivar" else "Reactivar") }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN ELIMINAR
                if (estado == "I") {
                    Button(
                        onClick = { mostrarDialogoEliminar = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Eliminar") }
                }

                // --- DIÁLOGOS ---
                if (mostrarDialogoInactivar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoInactivar = false },
                        title = { Text("Confirmar Inactivación") },
                        text = { Text("La categoría dejará de estar disponible para nuevos registros.") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.inactivarCategoria(catActual!!)
                                mostrarDialogoInactivar = false
                                onGuardarFinalizado()
                            }) { Text("Sí, inactivar") }
                        },
                        dismissButton = { TextButton(onClick = { mostrarDialogoInactivar = false }) { Text("Cancelar") } }
                    )
                }

                if (mostrarDialogoEliminar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },
                        title = { Text("¿Eliminar definitivamente?") },
                        text = { Text("La categoría pasará a estado eliminado (*).") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.eliminarCategoria(catActual!!)
                                mostrarDialogoEliminar = false
                                onGuardarFinalizado()
                            }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Eliminar") }
                        },
                        dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") } }
                    )
                }
            }
        }
    }
}