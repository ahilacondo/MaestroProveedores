package com.solucionesmoviles.proveedores.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel

// IMPORTAMOS TUS PANTALLAS REALES
import com.solucionesmoviles.proveedores.view.screens.LoginScreen
import com.solucionesmoviles.proveedores.view.screens.HomeScreen
import com.solucionesmoviles.proveedores.view.screens.ListaProveedoresScreen
import com.solucionesmoviles.proveedores.view.screens.FormularioProveedorScreen
import com.solucionesmoviles.proveedores.view.screens.SelectorPaisScreen
import com.solucionesmoviles.proveedores.view.screens.SelectorCategoriaScreen
import com.solucionesmoviles.proveedores.view.screens.ListaPaisesScreen
import com.solucionesmoviles.proveedores.view.screens.FormularioPaisScreen
import com.solucionesmoviles.proveedores.view.screens.ListaCategoriasScreen
import com.solucionesmoviles.proveedores.view.screens.FormularioCategoriaScreen

@Composable
fun AppNavigation(viewModel: ProveedorViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // 1. PANTALLA DE LOGIN (Inicio)
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. MENU PRINCIPAL (Home)
        composable("home") {
            HomeScreen(
                onNavegarA = { ruta -> navController.navigate(ruta) }
            )
        }

        // 3. MÓDULO PROVEEDORES (Lista)
        composable("proveedores") {
            ListaProveedoresScreen(
                viewModel = viewModel,
                onNuevoProveedor = { navController.navigate("crear_proveedor/0") },
                onEditarProveedor = { id -> navController.navigate("crear_proveedor/$id") },
                onVolver = { navController.popBackStack() },
                onNavegar = { ruta -> navController.navigate(ruta) }
            )
        }

        // 4. CREAR/EDITAR PROVEEDOR (Formulario)
        composable(
            route = "crear_proveedor/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0

            val savedStateHandle = entry.savedStateHandle
            val paisSeleccionadoId = savedStateHandle.get<Int>("pais_id")
            val categoriaSeleccionadaId = savedStateHandle.get<Int>("categoria_id")

            FormularioProveedorScreen(
                viewModel = viewModel,
                idProveedor = id,
                idPaisSeleccionado = paisSeleccionadoId,
                idCategoriaSeleccionada = categoriaSeleccionadaId,
                onSeleccionarPais = { navController.navigate("seleccion_pais") },
                onSeleccionarCategoria = { navController.navigate("seleccion_categoria") },
                onGuardarFinalizado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        // 5. SELECTORES (Pantallas Reales)
        composable("seleccion_pais") {
            SelectorPaisScreen(
                viewModel = viewModel,
                onPaisSeleccionado = { id ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("pais_id", id)
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // --- AQUÍ ESTABA EL ERROR: Solo debe haber UNA definición de seleccion_categoria ---
        composable("seleccion_categoria") {
            SelectorCategoriaScreen(
                viewModel = viewModel,
                onCategoriaSeleccionada = { id ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("categoria_id", id)
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // 6. MANTENIMIENTOS COMPLETOS

        // --- PAÍSES ---
        composable("paises_lista") {
            ListaPaisesScreen(
                viewModel = viewModel,
                onNuevoPais = { navController.navigate("crear_pais/0") },
                onEditarPais = { id -> navController.navigate("crear_pais/$id") },
                onNavegar = { ruta -> navController.navigate(ruta) }
            )
        }
        composable(
            route = "crear_pais/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            FormularioPaisScreen(
                viewModel = viewModel,
                idPais = id,
                onGuardarFinalizado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        // --- CATEGORÍAS ---
        composable("categorias_lista") {
            ListaCategoriasScreen(
                viewModel = viewModel,
                onNuevaCategoria = { navController.navigate("crear_categoria/0") },
                onEditarCategoria = { id -> navController.navigate("crear_categoria/$id") },
                onNavegar = { ruta -> navController.navigate(ruta) }
            )
        }
        composable(
            route = "crear_categoria/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            FormularioCategoriaScreen(
                viewModel = viewModel,
                idCategoria = id,
                onGuardarFinalizado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }
    }
}

