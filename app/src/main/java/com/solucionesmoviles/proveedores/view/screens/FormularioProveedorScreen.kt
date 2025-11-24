package com.solucionesmoviles.proveedores.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solucionesmoviles.proveedores.model.Proveedor
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProveedorScreen(
    viewModel: ProveedorViewModel,
    idProveedor: Int,
    idPaisSeleccionado: Int?,
    idCategoriaSeleccionada: Int?,
    onSeleccionarPais: () -> Unit,
    onSeleccionarCategoria: () -> Unit,
    onGuardarFinalizado: () -> Unit,
    onCancelar: () -> Unit
) {
    // 1. LEEMOS LOS DATOS DESDE EL VIEWMODEL
    val nombre = viewModel.nombreFormulario
    val ruc = viewModel.rucFormulario
    val tipoProveedor = viewModel.tipoFormulario

    val tiposDisponibles = listOf("Nacional", "Internacional", "Local", "Personal")
    var expandirMenuTipo by remember { mutableStateOf(false) }

    var selectedPaisId by remember { mutableIntStateOf(0) }
    var selectedCategoriaId by remember { mutableIntStateOf(0) }

    // CORRECCIÓN VISUAL: Inicializamos con "Seleccionar"
    var nombrePais by remember { mutableStateOf("Seleccionar") }
    var nombreCategoria by remember { mutableStateOf("Seleccionar") }

    var proveedorActual by remember { mutableStateOf<Proveedor?>(null) }
    val esEdicion = idProveedor != 0
    val esEliminado = proveedorActual?.estado == "*"
    val habilitado = !esEliminado

    // 2. CARGA INICIAL DE DATOS (EDICIÓN)
    LaunchedEffect(idProveedor) {
        if (esEdicion) {
            val prov = viewModel.getProveedorById(idProveedor)
            prov?.let {
                proveedorActual = it
                viewModel.cargarDatosParaEdicion(idProveedor, it)

                // Cargar IDs
                if (selectedPaisId == 0) selectedPaisId = it.paisId
                if (selectedCategoriaId == 0) selectedCategoriaId = it.categoriaId

                // CORRECCIÓN VISUAL: BUSCAMOS EL NOMBRE REAL EN LA BD
                // En lugar de poner "ID: 5", buscamos "Perú"
                val p = viewModel.getPaisById(it.paisId)
                nombrePais = p?.nombre ?: "Desconocido (ID: ${it.paisId})"

                val c = viewModel.getCategoriaById(it.categoriaId)
                nombreCategoria = c?.nombre ?: "Desconocido (ID: ${it.categoriaId})"
            }
        } else {
            if (viewModel.formularioCargadoId != 0) {
                viewModel.limpiarFormulario()
                viewModel.formularioCargadoId = 0
            }
        }
    }

    // 3. RECUPERAR SELECCIÓN DE PAÍS (AL VOLVER)
    LaunchedEffect(idPaisSeleccionado) {
        if (idPaisSeleccionado != null) {
            selectedPaisId = idPaisSeleccionado
            // CORRECCIÓN VISUAL: Buscamos el nombre del ID seleccionado
            val p = viewModel.getPaisById(idPaisSeleccionado)
            nombrePais = p?.nombre ?: "Cargando..."
        }
    }

    // 4. RECUPERAR SELECCIÓN DE CATEGORÍA (AL VOLVER)
    LaunchedEffect(idCategoriaSeleccionada) {
        if (idCategoriaSeleccionada != null) {
            selectedCategoriaId = idCategoriaSeleccionada
            // CORRECCIÓN VISUAL: Buscamos el nombre del ID seleccionado
            val c = viewModel.getCategoriaById(idCategoriaSeleccionada)
            nombreCategoria = c?.nombre ?: "Cargando..."
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar Proveedor" else "Crear Proveedor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) }
                },
                actions = {
                    if (habilitado) {
                        TextButton(onClick = {
                            if (nombre.isNotBlank() && ruc.isNotBlank() && selectedPaisId != 0 && selectedCategoriaId != 0) {
                                val nuevoProv = Proveedor(
                                    id = if (esEdicion) idProveedor else 0,
                                    nombre = nombre,
                                    ruc = ruc,
                                    tipoProveedor = tipoProveedor,
                                    paisId = selectedPaisId,
                                    categoriaId = selectedCategoriaId,
                                    estado = proveedorActual?.estado ?: "A"
                                )
                                viewModel.guardarProveedor(nuevoProv)
                                onGuardarFinalizado()
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
                        onChange = { viewModel.nombreFormulario = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.Gray)
                    CampoTextoSimple(
                        label = "RUC",
                        valor = ruc,
                        placeholder = "Ej: 201000...",
                        enabled = habilitado,
                        onChange = { viewModel.rucFormulario = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GRUPO 2: SELECTORES (Ahora mostrarán nombres bonitos)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectorItem(
                            label = "Tipo Prov.",
                            valor = tipoProveedor,
                            enabled = habilitado,
                            onClick = { expandirMenuTipo = true }
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd) // Pegado a la derecha
                                .padding(top = 40.dp, end = 16.dp) // Bajamos un poco para no tapar el texto
                        ) {
                            DropdownMenu(
                                expanded = expandirMenuTipo,
                                onDismissRequest = { expandirMenuTipo = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                tiposDisponibles.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            viewModel.tipoFormulario = tipo
                                            expandirMenuTipo = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                    // Aquí se verá "Categoría: Lácteos" en vez de "ID: 4"
                    SelectorItem(
                        label = "Categoría",
                        valor = nombreCategoria,
                        enabled = habilitado,
                        onClick = onSeleccionarCategoria
                    )

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                    // Aquí se verá "País: Perú" en vez de "ID: 2"
                    SelectorItem(
                        label = "País",
                        valor = nombrePais,
                        enabled = habilitado,
                        onClick = onSeleccionarPais
                    )
                }
            }

            if (esEdicion && proveedorActual != null) {
                Spacer(modifier = Modifier.height(32.dp))
                val estadoActual = proveedorActual!!.estado

                // DIÁLOGOS
                var mostrarDialogoInactivar by remember { mutableStateOf(false) }
                var mostrarDialogoEliminar by remember { mutableStateOf(false) }

                // BOTÓN REACTIVAR / INACTIVAR
                Button(
                    onClick = {
                        if (estadoActual == "A") {
                            mostrarDialogoInactivar = true
                        } else {
                            viewModel.reactivarProveedor(proveedorActual!!)
                            onGuardarFinalizado()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (estadoActual == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                        contentColor = if (estadoActual == "A") Color(0xFFD97706) else Color(0xFF166534)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (estadoActual == "A") "Inactivar Proveedor" else "Reactivar Proveedor")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN ELIMINAR (Solo si es INACTIVO)
                if (estadoActual == "I") {
                    Button(
                        onClick = { mostrarDialogoEliminar = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Eliminar Proveedor")
                    }
                }

                if (mostrarDialogoInactivar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoInactivar = false },
                        title = { Text("¿Inactivar Proveedor?", fontWeight = FontWeight.Bold) },
                        text = { Text("El proveedor dejará de estar disponible para nuevas operaciones.") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.inactivarProveedor(proveedorActual!!)
                                mostrarDialogoInactivar = false
                                onGuardarFinalizado()
                            }) { Text("Sí, inactivar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogoInactivar = false }) { Text("Cancelar") }
                        }
                    )
                }

                if (mostrarDialogoEliminar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },
                        title = { Text("¿Eliminar definitivamente?", fontWeight = FontWeight.Bold) },
                        text = { Text("El registro se marcará como eliminado y se archivará al final de la lista.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.eliminarProveedor(proveedorActual!!)
                                    mostrarDialogoEliminar = false
                                    onGuardarFinalizado()
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                            ) { Text("Sí, eliminar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") }
                        }
                    )
                }
            }
        }
    }
}

// COMPONENTES (Igual que antes)
@Composable
fun CampoTextoSimple(
    label: String,
    valor: String,
    placeholder: String,
    enabled: Boolean = true,
    onChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        TextField(
            value = valor,
            onValueChange = onChange,
            enabled = enabled,
            placeholder = { Text(placeholder, color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = Color.Gray,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledPlaceholderColor = Color.LightGray
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SelectorItem(
    label: String,
    valor: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valor, color = if (enabled) Color.Gray else Color.LightGray)
            Spacer(modifier = Modifier.width(8.dp))
            if (enabled) {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
        }
    }
}