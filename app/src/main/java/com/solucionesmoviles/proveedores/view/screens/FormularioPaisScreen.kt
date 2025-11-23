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

    LaunchedEffect(idPais) {
        if (esEdicion) {
            val p = viewModel.getPaisById(idPais)
            p?.let {
                paisActual = it
                codigoActual = it.codigo
                nombre = it.nombre
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esEdicion) "Editar País" else "Crear País", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = onCancelar) { Text("Cancelar", color = MaterialTheme.colorScheme.primary) } },
                actions = {
                    TextButton(onClick = {
                        if (nombre.isNotBlank()) {
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
                    }) { Text("Guardar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
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
                    CampoTextoSimple(label = "Nombre", valor = nombre, placeholder = "Ej: Perú", onChange = { nombre = it })
                }
            }

            if (esEdicion && paisActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val estado = paisActual!!.estado

                Button(
                    onClick = {
                        if (estado == "A") viewModel.inactivarPais(paisActual!!) else viewModel.reactivarPais(paisActual!!)
                        onGuardarFinalizado()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (estado == "A") Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                        contentColor = if (estado == "A") Color(0xFFD97706) else Color(0xFF166534)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (estado == "A") "Inactivar País" else "Reactivar País") }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.eliminarPais(paisActual!!); onGuardarFinalizado() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFF991B1B)),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Eliminar País") }
            }
        }
    }
}