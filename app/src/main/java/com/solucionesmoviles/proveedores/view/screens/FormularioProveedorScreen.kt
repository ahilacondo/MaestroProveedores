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
    var nombre by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var tipoProveedor by remember { mutableStateOf("Nacional") }

    val tiposDisponibles = listOf("Nacional", "Internacional", "Local", "Personal")
    var expandirMenuTipo by remember { mutableStateOf(false) }

    var selectedPaisId by remember { mutableIntStateOf(0) }
    var selectedCategoriaId by remember { mutableIntStateOf(0) }
    var nombrePais by remember { mutableStateOf("Seleccionar") }
    var nombreCategoria by remember { mutableStateOf("Seleccionar") }

    var proveedorActual by remember { mutableStateOf<Proveedor?>(null) }
    val esEdicion = idProveedor != 0

    LaunchedEffect(idProveedor) {
        if (esEdicion) {
            val prov = viewModel.getProveedorById(idProveedor)
            prov?.let {
                proveedorActual = it
                nombre = it.nombre
                ruc = it.ruc
                tipoProveedor = it.tipoProveedor
                selectedPaisId = it.paisId
                selectedCategoriaId = it.categoriaId
                nombrePais = "ID Registrado: ${it.paisId}"
                nombreCategoria = "ID Registrado: ${it.categoriaId}"
            }
        }
    }

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
                    CampoTextoSimple(label = "Nombre", valor = nombre, placeholder = "Ej: Molitalia S.A.", onChange = { nombre = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.Gray)
                    CampoTextoSimple(label = "RUC", valor = ruc, placeholder = "Ej: 201000...", onChange = { ruc = it })
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
                                        tipoProveedor = tipo
                                        expandirMenuTipo = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                    SelectorItem(label = "Categoría", valor = nombreCategoria, onClick = onSeleccionarCategoria)
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                    SelectorItem(label = "País", valor = nombrePais, onClick = onSeleccionarPais)
                }
            }

            if (esEdicion && proveedorActual != null) {
                Spacer(modifier = Modifier.height(32.dp))
                val estadoActual = proveedorActual!!.estado

                Button(
                    onClick = {
                        if (estadoActual == "A") viewModel.inactivarProveedor(proveedorActual!!)
                        else viewModel.reactivarProveedor(proveedorActual!!)
                        onGuardarFinalizado()
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

                Button(
                    onClick = {
                        viewModel.eliminarProveedor(proveedorActual!!)
                        onGuardarFinalizado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar Proveedor")
                }
            }
        }
    }
}

@Composable
fun CampoTextoSimple(label: String, valor: String, placeholder: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurface // COLOR DINÁMICO
        )
        TextField(
            value = valor,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                // Texto que escribe el usuario
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SelectorItem(label: String, valor: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valor, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        }
    }
}