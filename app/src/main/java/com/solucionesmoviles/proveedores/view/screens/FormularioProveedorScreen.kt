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
    // 1. LEEMOS LOS DATOS DESDE EL VIEWMODEL (Persistencia al navegar)
    val nombre = viewModel.nombreFormulario
    val ruc = viewModel.rucFormulario
    val tipoProveedor = viewModel.tipoFormulario

    val tiposDisponibles = listOf("Nacional", "Internacional", "Local", "Personal")
    var expandirMenuTipo by remember { mutableStateOf(false) }

    var selectedPaisId by remember { mutableIntStateOf(0) }
    var selectedCategoriaId by remember { mutableIntStateOf(0) }
    var nombrePais by remember { mutableStateOf("Seleccionar") }
    var nombreCategoria by remember { mutableStateOf("Seleccionar") }

    var proveedorActual by remember { mutableStateOf<Proveedor?>(null) }
    val esEdicion = idProveedor != 0

    // LÓGICA DE BLOQUEO: Si el estado es "*" (Eliminado), NO se puede editar.
    val esEliminado = proveedorActual?.estado == "*"
    val habilitado = !esEliminado

    // 2. CARGA INICIAL DE DATOS
    LaunchedEffect(idProveedor) {
        if (esEdicion) {
            val prov = viewModel.getProveedorById(idProveedor)
            prov?.let {
                proveedorActual = it
                // Cargamos datos al ViewModel para que persistan
                viewModel.cargarDatosParaEdicion(idProveedor, it)

                // Cargamos IDs de selectores (si aún no se han seleccionado otros)
                if (selectedPaisId == 0) selectedPaisId = it.paisId
                if (selectedCategoriaId == 0) selectedCategoriaId = it.categoriaId
                nombrePais = "ID Registrado: ${it.paisId}"
                nombreCategoria = "ID Registrado: ${it.categoriaId}"
            }
        } else {
            // Si es Nuevo, limpiamos el ViewModel si venimos de otra edición
            if (viewModel.formularioCargadoId != 0) {
                viewModel.limpiarFormulario()
                viewModel.formularioCargadoId = 0
            }
        }
    }

    // 3. RECUPERAR SELECCIONES AL VOLVER DE OTRA PANTALLA
    LaunchedEffect(idPaisSeleccionado) {
        if (idPaisSeleccionado != null) {
            selectedPaisId = idPaisSeleccionado
            nombrePais = "País Seleccionado (ID: $idPaisSeleccionado)"
        }
    }

    LaunchedEffect(idCategoriaSeleccionada) {
        if (idCategoriaSeleccionada != null) {
            selectedCategoriaId = idCategoriaSeleccionada
            nombreCategoria = "Categoría Seleccionada (ID: $idCategoriaSeleccionada)"
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
                    // Solo mostramos el botón "Guardar" si está habilitado (NO eliminado)
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
                        enabled = habilitado, // <--- BLOQUEADO SI ELIMINADO
                        onChange = { viewModel.nombreFormulario = it } // Guardamos en ViewModel
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.Gray)
                    CampoTextoSimple(
                        label = "RUC",
                        valor = ruc,
                        placeholder = "Ej: 201000...",
                        enabled = habilitado, // <--- BLOQUEADO SI ELIMINADO
                        onChange = { viewModel.rucFormulario = it } // Guardamos en ViewModel
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
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectorItem(
                            label = "Tipo Prov.",
                            valor = tipoProveedor,
                            enabled = habilitado, // <--- BLOQUEADO SI ELIMINADO
                            onClick = { expandirMenuTipo = true }
                        )

                        DropdownMenu(
                            expanded = expandirMenuTipo,
                            onDismissRequest = { expandirMenuTipo = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            tiposDisponibles.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.tipoFormulario = tipo // Guardamos en ViewModel
                                        expandirMenuTipo = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                    SelectorItem(
                        label = "Categoría",
                        valor = nombreCategoria,
                        enabled = habilitado, // <--- BLOQUEADO SI ELIMINADO
                        onClick = onSeleccionarCategoria
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                    SelectorItem(
                        label = "País",
                        valor = nombrePais,
                        enabled = habilitado, // <--- BLOQUEADO SI ELIMINADO
                        onClick = onSeleccionarPais
                    )
                }
            }

            if (esEdicion && proveedorActual != null) {
                Spacer(modifier = Modifier.height(32.dp))
                val estadoActual = proveedorActual!!.estado

                // Variables para controlar los diálogos
                var mostrarDialogoInactivar by remember { mutableStateOf(false) }
                var mostrarDialogoEliminar by remember { mutableStateOf(false) }

                // BOTÓN 1: REACTIVAR / INACTIVAR
                Button(
                    onClick = {
                        if (estadoActual == "A") {
                            // Si está activo, pedimos confirmación antes de inactivar
                            mostrarDialogoInactivar = true
                        } else {
                            // Si ya está inactivo o eliminado, reactivamos directamente
                            viewModel.reactivarProveedor(proveedorActual!!)
                            onGuardarFinalizado()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        // Amarillo si es para Inactivar, Verde si es para Reactivar
                        containerColor = if (estadoActual == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                        contentColor = if (estadoActual == "A") Color(0xFFD97706) else Color(0xFF166534)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (estadoActual == "A") "Inactivar Proveedor" else "Reactivar Proveedor")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN 2: ELIMINAR (Solo visible si está INACTIVO "I")
                // No se puede eliminar directamente un Activo ("A") ni re-eliminar un Eliminado ("*")
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

                // --- DIÁLOGOS DE CONFIRMACIÓN ---

                if (mostrarDialogoInactivar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoInactivar = false },
                        title = { Text("¿Inactivar Proveedor?", fontWeight = FontWeight.Bold) },
                        text = { Text("El proveedor dejará de estar disponible para nuevas operaciones, pero conservará su historial.") },
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
                        text = { Text("El registro se marcará como eliminado y se archivará al final de la lista. Esta acción requiere reactivación manual para deshacerse.") },
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

// COMPONENTES PERSONALIZADOS ACTUALIZADOS

@Composable
fun CampoTextoSimple(
    label: String,
    valor: String,
    placeholder: String,
    enabled: Boolean = true, // Parámetro para bloquear
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
            enabled = enabled, // Bloqueo aquí
            placeholder = { Text(placeholder, color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                // Colores para estado deshabilitado (Read-only)
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
    enabled: Boolean = true, // Parámetro para bloquear
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick) // Bloqueo de click
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valor, color = if (enabled) Color.Gray else Color.LightGray) // Feedback visual
            Spacer(modifier = Modifier.width(8.dp))
            if (enabled) { // Solo mostrar flecha si se puede editar
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
        }
    }
}