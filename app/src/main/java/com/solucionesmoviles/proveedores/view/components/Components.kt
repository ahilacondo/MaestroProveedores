package com.solucionesmoviles.proveedores.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. CAMPO DE TEXTO SIMPLE (Sin marcos, estilo limpio)
@Composable
fun CampoTextoSimple(
    label: String,
    valor: String,
    placeholder: String,
    enabled: Boolean = true,
    esNumerico: Boolean = false,
    isError: Boolean = false,
    errorText: String? = null,
    onChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(100.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            // CAMBIO: Usamos TextField en lugar de OutlinedTextField
            // y configuramos los colores transparentes para quitar el marco.
            TextField(
                value = valor,
                onValueChange = { input ->
                    if (esNumerico) {
                        if (input.all { it.isDigit() }) onChange(input)
                    } else {
                        onChange(input)
                    }
                },
                enabled = enabled,
                placeholder = { Text(placeholder, color = Color.Gray) },
                isError = isError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (esNumerico) KeyboardType.Number else KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                // AQUÍ QUITAMOS LOS BORDES Y COLORES DE FONDO
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,

                    focusedIndicatorColor = Color.Transparent,   // Sin línea abajo al escribir
                    unfocusedIndicatorColor = Color.Transparent, // Sin línea abajo normal
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,     // Sin línea roja (solo texto rojo)

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        // Mantenemos el mensaje de error pequeño si existe
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 100.dp, top = 0.dp)
            )
        }
    }
}

// 2. ITEM SELECTOR
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