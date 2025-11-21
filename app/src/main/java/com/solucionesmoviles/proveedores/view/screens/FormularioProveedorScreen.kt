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
    // --- 1. ESTADOS DEL FORMULARIO ---
    var nombre by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var tipoProveedor by remember { mutableStateOf("Nacional") } // Valor por defecto

    // NUEVO: Variables para el menú desplegable
    val tiposDisponibles = listOf("Nacional", "Internacional", "Local", "Personal")
    var expandirMenuTipo by remember { mutableStateOf(false) }

    // IDs y Nombres para mostrar
    var selectedPaisId by remember { mutableIntStateOf(0) }
    var selectedCategoriaId by remember { mutableIntStateOf(0) }
    var nombrePais by remember { mutableStateOf("Seleccionar") }
    var nombreCategoria by remember { mutableStateOf("Seleccionar") }

    // Control de Edición
    var proveedorActual by remember { mutableStateOf<Proveedor?>(null) }
    val esEdicion = idProveedor != 0

    // --- 2. LÓGICA DE CARGA (Si es edición) ---
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

                // Intentamos cargar nombres bonitos (idealmente vendrían de la BD)
                // Por ahora mostramos el ID para confirmar que funciona
                nombrePais = "ID Registrado: ${it.paisId}"
                nombreCategoria = "ID Registrado: ${it.categoriaId}"
            }
        }
    }

    // --- 3. RESPUESTA DE LOS SELECTORES (Cuando vuelves de elegir País/Cat) ---
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
                    TextButton(onClick = onCancelar) { Text("Cancelar", color = Color(0xFF2563EB)) }
                },
                actions = {
                    TextButton(onClick = {
                        // VALIDACIÓN
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
                        Text("Guardar", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // GRUPO 1: DATOS BÁSICOS
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CampoTextoSimple(label = "Nombre", valor = nombre, placeholder = "Ej: Molitalia S.A.", onChange = { nombre = it })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                    CampoTextoSimple(label = "RUC", valor = ruc, placeholder = "Ej: 201000...", onChange = { ruc = it })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GRUPO 2: SELECTORES (Aquí está tu cambio)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    // A. MENU DESPLEGABLE (DROPDOWN)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectorItem(
                            label = "Tipo Prov.",
                            valor = tipoProveedor,
                            onClick = { expandirMenuTipo = true }
                        )

                        DropdownMenu(
                            expanded = expandirMenuTipo,
                            onDismissRequest = { expandirMenuTipo = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            tiposDisponibles.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        tipoProveedor = tipo
                                        expandirMenuTipo = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

                    // B. SELECTOR DE CATEGORÍA (Navegación)
                    SelectorItem(label = "Categoría", valor = nombreCategoria, onClick = onSeleccionarCategoria)

                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

                    // C. SELECTOR DE PAÍS (Navegación)
                    SelectorItem(label = "País", valor = nombrePais, onClick = onSeleccionarPais)
                }
            }

            // BOTONES DE ACCIÓN (Solo en edición)
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

// COMPONENTES REUTILIZABLES
@Composable
fun CampoTextoSimple(label: String, valor: String, placeholder: String, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
        TextField(
            value = valor,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
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
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valor, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
        }
    }
}