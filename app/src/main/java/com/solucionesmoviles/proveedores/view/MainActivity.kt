package com.solucionesmoviles.proveedores.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.theme.MaestroProveedoresTheme // Asegúrate que este import coincida con tu paquete theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciamos el ViewModel aquí (sobrevive a giros de pantalla)
        val viewModel: ProveedorViewModel by viewModels()

        setContent {
            MaestroProveedoresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ¡Aquí arranca tu app!
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}