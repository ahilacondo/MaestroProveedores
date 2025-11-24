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
import com.solucionesmoviles.proveedores.model.Proveedor
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.components.CampoTextoSimple
import com.solucionesmoviles.proveedores.view.components.SelectorItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProveedorScreen(
    viewModel: ProveedorViewModel,
    idProveedor: Int,
    idPaisSeleccionado: Int?,
    idCategoriaSeleccionada: Int?,
    idTipoSeleccionado: Int?,
    onSeleccionarPais: () -> Unit,
    onSeleccionarCategoria: () -> Unit,
    onSeleccionarTipo: () -> Unit,
    onGuardarFinalizado: () -> Unit,
    onCancelar: () -> Unit
) {
    // VARIABLES DEL VIEWMODEL
    val nombre = viewModel.nombreFormulario
    val ruc = viewModel.rucFormulario

    // VARIABLES DE VALIDACIÓN
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorRuc by remember { mutableStateOf<String?>(null) }
    var intentoGuardar by remember { mutableStateOf(false) }

    // VARIABLES DE SELECCIÓN
    var selectedPaisId by remember { mutableIntStateOf(0) }
    var selectedCategoriaId by remember { mutableIntStateOf(0) }
    var selectedTipoId by remember { mutableIntStateOf(0) }

    // NOMBRES VISUALES
    var nombrePais by remember { mutableStateOf("Seleccionar") }
    var nombreCategoria by remember { mutableStateOf("Seleccionar") }
    var nombreTipo by remember { mutableStateOf("Seleccionar") }

    var proveedorActual by remember { mutableStateOf<Proveedor?>(null) }
    val esEdicion = idProveedor != 0
    val esEliminado = proveedorActual?.estado == "*"
    val habilitado = !esEliminado

    // ESTADOS DE DIÁLOGOS
    var mostrarDialogoInactivar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoGuardar by remember { mutableStateOf(false) } // <--- NUEVO ESTADO

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // FUNCIÓN DE VALIDACIÓN
    fun validar(): Boolean {
        var esValido = true

        // Validar Nombre
        if (nombre.length < 3) {
            errorNombre = "Mínimo 3 caracteres"
            esValido = false
        } else if (!nombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 .&-]+$"))) {
            errorNombre = "Caracteres inválidos"
            esValido = false
        } else {
            errorNombre = null
        }

        // Validar RUC
        if (ruc.length != 11) {
            errorRuc = "Debe tener 11 dígitos"
            esValido = false
        } else {
            errorRuc = null
        }

        return esValido
    }

    // CARGAS INICIALES
    LaunchedEffect(idProveedor) {
        if (esEdicion) {
            val prov = viewModel.getProveedorById(idProveedor)
            prov?.let {
                proveedorActual = it
                viewModel.nombreFormulario = it.nombre
                viewModel.rucFormulario = it.ruc
                selectedPaisId = it.paisId
                selectedCategoriaId = it.categoriaId
                selectedTipoId = it.tipoProveedorId

                val p = viewModel.getPaisById(it.paisId)
                nombrePais = p?.nombre ?: "Desconocido"
                val c = viewModel.getCategoriaById(it.categoriaId)
                nombreCategoria = c?.nombre ?: "Desconocido"
                val t = viewModel.getTipoProveedorById(it.tipoProveedorId)
                nombreTipo = t?.nombre ?: "Desconocido"
            }
        } else {
            if (viewModel.formularioCargadoId != 0) {
                viewModel.limpiarFormulario()
                viewModel.formularioCargadoId = 0
            }
        }
    }

    // RECUPERAR SELECCIONES
    LaunchedEffect(idPaisSeleccionado) {
        if (idPaisSeleccionado != null) {
            selectedPaisId = idPaisSeleccionado
            val p = viewModel.getPaisById(idPaisSeleccionado)
            nombrePais = p?.nombre ?: "Cargando..."
        }
    }
    LaunchedEffect(idCategoriaSeleccionada) {
        if (idCategoriaSeleccionada != null) {
            selectedCategoriaId = idCategoriaSeleccionada
            val c = viewModel.getCategoriaById(idCategoriaSeleccionada)
            nombreCategoria = c?.nombre ?: "Cargando..."
        }
    }
    LaunchedEffect(idTipoSeleccionado) {
        if (idTipoSeleccionado != null) {
            selectedTipoId = idTipoSeleccionado
            val t = viewModel.getTipoProveedorById(idTipoSeleccionado)
            nombreTipo = t?.nombre ?: "Cargando..."
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar Proveedor" else "Crear Proveedor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) }
                },
                actions = {
                    if (habilitado) {
                        TextButton(onClick = {
                            intentoGuardar = true
                            val formularioValido = validar()
                            val selectoresValidos = selectedPaisId != 0 && selectedCategoriaId != 0 && selectedTipoId != 0

                            if (formularioValido && selectoresValidos) {
                                // EN LUGAR DE GUARDAR DIRECTO, MOSTRAMOS EL DIÁLOGO
                                mostrarDialogoGuardar = true
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Faltan campos por completar")
                                }
                            }
                        }) {
                            Text("Guardar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // GRUPO 1: DATOS BÁSICOS
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CampoTextoSimple(
                        label = "Nombre",
                        valor = nombre,
                        placeholder = "Ej: Molitalia S.A.",
                        enabled = habilitado,
                        isError = errorNombre != null,
                        errorText = errorNombre,
                        onChange = {
                            viewModel.nombreFormulario = it
                            errorNombre = null
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.Gray)
                    CampoTextoSimple(
                        label = "RUC",
                        valor = ruc,
                        placeholder = "Ej: 201000...",
                        enabled = habilitado,
                        esNumerico = true,
                        isError = errorRuc != null,
                        errorText = errorRuc,
                        onChange = {
                            if (it.length <= 11) viewModel.rucFormulario = it
                            errorRuc = null
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GRUPO 2: SELECTORES
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SelectorItem("Tipo Prov.", nombreTipo, habilitado, onSeleccionarTipo)
                    if (intentoGuardar && selectedTipoId == 0) Text("Requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                    SelectorItem("Categoría", nombreCategoria, habilitado, onSeleccionarCategoria)
                    if (intentoGuardar && selectedCategoriaId == 0) Text("Requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                    SelectorItem("País", nombrePais, habilitado, onSeleccionarPais)
                    if (intentoGuardar && selectedPaisId == 0) Text("Requerido", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                }
            }

            // GRUPO 3: BOTONES
            if (esEdicion && proveedorActual != null) {
                Spacer(modifier = Modifier.height(32.dp))
                val estadoActual = proveedorActual!!.estado

                Button(
                    onClick = {
                        if (estadoActual == "A") mostrarDialogoInactivar = true
                        else { viewModel.reactivarProveedor(proveedorActual!!); onGuardarFinalizado() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (estadoActual == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7), contentColor = if (estadoActual == "A") Color(0xFFD97706) else Color(0xFF166534)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(if (estadoActual == "A") "Inactivar Proveedor" else "Reactivar Proveedor") }

                if (estadoActual == "I") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { mostrarDialogoEliminar = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("Eliminar Proveedor") }
                }
            }

            // --- NUEVO DIÁLOGO DE GUARDAR ---
            if (mostrarDialogoGuardar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoGuardar = false },
                    title = { Text("¿Guardar cambios?") },
                    text = { Text(if (esEdicion) "¿Estás seguro de modificar este proveedor?" else "¿Estás seguro de registrar este nuevo proveedor?") },
                    confirmButton = {
                        TextButton(onClick = {
                            // AQUÍ REALMENTE SE GUARDA
                            val nuevoProv = Proveedor(
                                id = if (esEdicion) idProveedor else 0,
                                nombre = nombre,
                                ruc = ruc,
                                tipoProveedorId = selectedTipoId,
                                paisId = selectedPaisId,
                                categoriaId = selectedCategoriaId,
                                estado = proveedorActual?.estado ?: "A"
                            )
                            viewModel.guardarProveedor(nuevoProv)
                            mostrarDialogoGuardar = false
                            onGuardarFinalizado()
                        }) { Text("Sí, guardar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoGuardar = false }) { Text("Cancelar") }
                    }
                )
            }

            // OTROS DIÁLOGOS
            if (mostrarDialogoInactivar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoInactivar = false },
                    title = { Text("¿Inactivar Proveedor?") },
                    text = { Text("El proveedor dejará de estar disponible.") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.inactivarProveedor(proveedorActual!!); mostrarDialogoInactivar = false; onGuardarFinalizado() }) { Text("Sí, inactivar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarDialogoInactivar = false }) { Text("Cancelar") } }
                )
            }

            if (mostrarDialogoEliminar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoEliminar = false },
                    title = { Text("¿Eliminar definitivamente?") },
                    text = { Text("El registro se marcará como eliminado.") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.eliminarProveedor(proveedorActual!!); mostrarDialogoEliminar = false; onGuardarFinalizado() }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Sí, eliminar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") } }
                )
            }
        }
    }
}