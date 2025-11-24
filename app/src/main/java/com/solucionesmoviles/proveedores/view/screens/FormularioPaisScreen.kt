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
import com.solucionesmoviles.proveedores.model.Pais
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.components.CampoTextoSimple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioPaisScreen(
    viewModel: ProveedorViewModel,
    idPais: Int,
    onGuardarFinalizado: () -> Unit,
    onCancelar: () -> Unit
) {
    var codigoActual by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var paisActual by remember { mutableStateOf<Pais?>(null) }
    val esEdicion = idPais != 0
    var errorNombre by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoInactivar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }

    val esEliminado = paisActual?.estado == "*"
    val habilitado = !esEliminado

    fun validar(): Boolean {
        if (nombre.isBlank()) { errorNombre = "El nombre es obligatorio"; return false }
        if (nombre.length < 3) { errorNombre = "Mínimo 3 letras"; return false }
        if (!nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) { errorNombre = "No se permiten números ni símbolos"; return false }
        return true
    }

    val guardarEnBD = {
        viewModel.guardarPais(
            Pais(
                id = if (esEdicion) idPais else 0,
                codigo = if (esEdicion) codigoActual else "",
                nombre = nombre,
                estado = paisActual?.estado ?: "A"
            )
        )
        onGuardarFinalizado()
    }

    LaunchedEffect(idPais) {
        if (esEdicion) {
            val p = viewModel.getPaisById(idPais)
            p?.let { paisActual = it; codigoActual = it.codigo; nombre = it.nombre }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar País" else "Crear País", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) } },
                actions = {
                    if (habilitado) {
                        TextButton(onClick = {
                            if (validar()) {
                                scope.launch {
                                    if (viewModel.existePais(nombre, idPais)) {
                                        errorNombre = "Este nombre ya existe"
                                    } else {
                                        // Si no existe duplicado, procedemos
                                        if (esEdicion) mostrarDialogoGuardar = true
                                        else guardarEnBD()
                                    }
                                }
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
                    CampoTextoSimple(label = "Nombre", valor = nombre, placeholder = "Ej: Perú", enabled = habilitado, isError = errorNombre != null, errorText = errorNombre, onChange = { nombre = it; errorNombre = null })
                }
            }

            if (esEdicion && paisActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val estado = paisActual!!.estado
                Button(
                    onClick = {
                        if (estado == "A") {
                            scope.launch { if (viewModel.puedeInactivarPais(idPais)) mostrarDialogoInactivar = true else snackbarHostState.showSnackbar("Error: Hay proveedores activos usando este país.") }
                        } else { viewModel.reactivarPais(paisActual!!); onGuardarFinalizado() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (estado == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7), contentColor = if (estado == "A") Color(0xFFD97706) else Color(0xFF166534)),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                ) { Text(if (estado == "A") "Inactivar País" else "Reactivar País") }

                if (estado == "I") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { mostrarDialogoEliminar = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Eliminar País") }
                }

                if (mostrarDialogoGuardar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoGuardar = false },
                        title = { Text("¿Guardar cambios?") },
                        text = { Text("¿Estás seguro de modificar este país?") },
                        confirmButton = { TextButton(onClick = { guardarEnBD(); mostrarDialogoGuardar = false }) { Text("Sí, guardar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoGuardar = false }) { Text("Cancelar") } }
                    )
                }
                if (mostrarDialogoInactivar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoInactivar = false },
                        title = { Text("Confirmar Inactivación") },
                        text = { Text("El país dejará de aparecer en los selectores.") },
                        confirmButton = { TextButton(onClick = { viewModel.inactivarPais(paisActual!!); mostrarDialogoInactivar = false; onGuardarFinalizado() }) { Text("Sí, inactivar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoInactivar = false }) { Text("Cancelar") } }
                    )
                }
                if (mostrarDialogoEliminar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },
                        title = { Text("¿Eliminar definitivamente?") },
                        text = { Text("El registro se marcará como eliminado (*).") },
                        confirmButton = { TextButton(onClick = { viewModel.eliminarPais(paisActual!!); mostrarDialogoEliminar = false; onGuardarFinalizado() }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Eliminar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") } }
                    )
                }
            }
        }
    }
}