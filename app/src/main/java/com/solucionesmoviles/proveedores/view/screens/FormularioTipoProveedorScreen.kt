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
import com.solucionesmoviles.proveedores.model.TipoProveedor
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.components.CampoTextoSimple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTipoProveedorScreen(
    viewModel: ProveedorViewModel,
    idTipo: Int,
    onGuardarFinalizado: () -> Unit,
    onCancelar: () -> Unit
) {
    var codigoActual by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var tipoActual by remember { mutableStateOf<TipoProveedor?>(null) }
    val esEdicion = idTipo != 0

    var errorNombre by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // Necesario para alertas de integridad

    var mostrarDialogoInactivar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }

    val esEliminado = tipoActual?.estado == "*"
    val habilitado = !esEliminado

    fun validar(): Boolean {
        if (nombre.isBlank()) { errorNombre = "El nombre es obligatorio"; return false }
        if (nombre.length < 3) { errorNombre = "Mínimo 3 letras"; return false }
        if (!nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) { errorNombre = "No se permiten números ni símbolos"; return false }
        return true
    }

    val guardarEnBD = {
        viewModel.guardarTipoProveedor(
            TipoProveedor(
                id = if (esEdicion) idTipo else 0,
                codigo = if (esEdicion) codigoActual else "",
                nombre = nombre,
                estado = tipoActual?.estado ?: "A"
            )
        )
        onGuardarFinalizado()
    }

    LaunchedEffect(idTipo) {
        if (esEdicion) {
            val t = viewModel.getTipoProveedorById(idTipo)
            t?.let { tipoActual = it; codigoActual = it.codigo; nombre = it.nombre }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar Tipo Prov." else "Crear Tipo Prov.", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) } },
                actions = {
                    if (habilitado) {
                        TextButton(onClick = {
                            if (validar()) {
                                // VALIDAR DUPLICADOS EN BD
                                scope.launch {
                                    if (viewModel.existeTipoProveedor(nombre, idTipo)) {
                                        errorNombre = "Este nombre ya existe"
                                    } else {
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
                    CampoTextoSimple(label = "Nombre", valor = nombre, placeholder = "Ej: Internacional", enabled = habilitado, isError = errorNombre != null, errorText = errorNombre, onChange = { nombre = it; errorNombre = null })
                }
            }

            if (esEdicion && tipoActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val estado = tipoActual!!.estado
                Button(
                    onClick = {
                        if (estado == "A") {
                            scope.launch {
                                // Validar si se puede inactivar (integridad referencial)
                                if (viewModel.puedeInactivarTipo(idTipo)) mostrarDialogoInactivar = true
                                else snackbarHostState.showSnackbar("Error: Hay proveedores activos usando este tipo.")
                            }
                        } else { viewModel.reactivarTipoProveedor(tipoActual!!); onGuardarFinalizado() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (estado == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7), contentColor = if (estado == "A") Color(0xFFD97706) else Color(0xFF166534)),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                ) { Text(if (estado == "A") "Inactivar" else "Reactivar") }

                if (estado == "I") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { mostrarDialogoEliminar = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Eliminar") }
                }

                if (mostrarDialogoGuardar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoGuardar = false },
                        title = { Text("¿Guardar cambios?") },
                        text = { Text("¿Estás seguro de modificar este tipo?") },
                        confirmButton = { TextButton(onClick = { guardarEnBD(); mostrarDialogoGuardar = false }) { Text("Sí, guardar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoGuardar = false }) { Text("Cancelar") } }
                    )
                }
                if (mostrarDialogoInactivar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoInactivar = false },
                        title = { Text("Confirmar Inactivación") },
                        text = { Text("Este tipo dejará de estar disponible.") },
                        confirmButton = { TextButton(onClick = { viewModel.inactivarTipoProveedor(tipoActual!!); mostrarDialogoInactivar = false; onGuardarFinalizado() }) { Text("Sí, inactivar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoInactivar = false }) { Text("Cancelar") } }
                    )
                }
                if (mostrarDialogoEliminar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },
                        title = { Text("¿Eliminar definitivamente?") },
                        text = { Text("El registro pasará a estado eliminado (*).") },
                        confirmButton = { TextButton(onClick = { viewModel.eliminarTipoProveedor(tipoActual!!); mostrarDialogoEliminar = false; onGuardarFinalizado() }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Eliminar") } },
                        dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") } }
                    )
                }
            }
        }
    }
}