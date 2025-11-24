package com.solucionesmoviles.proveedores.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.solucionesmoviles.proveedores.viewmodel.ProveedorViewModel
import com.solucionesmoviles.proveedores.view.screens.*

@Composable
fun AppNavigation(viewModel: ProveedorViewModel) {
    val navController = rememberNavController()

    // 1. CONFIGURACIÓN INICIAL: Arranca directo en "home" (Login eliminado)
    NavHost(navController = navController, startDestination = "home") {

        // 2. MENU PRINCIPAL (Home)
        composable("home") {
            HomeScreen(
                onNavegarA = { ruta -> navController.navigate(ruta) }
            )
        }
        composable("ajustes") {
            AjustesScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
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

            // Recuperamos los IDs seleccionados de los selectores
            val savedStateHandle = entry.savedStateHandle
            val paisSeleccionadoId = savedStateHandle.get<Int>("pais_id")
            val categoriaSeleccionadaId = savedStateHandle.get<Int>("categoria_id")
            val tipoSeleccionadoId = savedStateHandle.get<Int>("tipo_id") // <--- NUEVO

            FormularioProveedorScreen(
                viewModel = viewModel,
                idProveedor = id,
                idPaisSeleccionado = paisSeleccionadoId,
                idCategoriaSeleccionada = categoriaSeleccionadaId,
                idTipoSeleccionado = tipoSeleccionadoId, // <--- PASAMOS EL ID
                onSeleccionarPais = { navController.navigate("seleccion_pais") },
                onSeleccionarCategoria = { navController.navigate("seleccion_categoria") },
                onSeleccionarTipo = { navController.navigate("seleccion_tipo") }, // <--- NUEVA RUTA
                onGuardarFinalizado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        // 5. SELECTORES (Pantallas para elegir País, Categoría y Tipo)
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

        composable("seleccion_tipo") { // <--- NUEVA PANTALLA DE SELECCIÓN
            SelectorTipoProveedorScreen(
                viewModel = viewModel,
                onTipoSeleccionado = { id ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("tipo_id", id)
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // 6. MANTENIMIENTOS COMPLETOS (Tablas auxiliares)

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

        // --- TIPOS DE PROVEEDOR ---
        composable("tipos_lista") {
            ListaTipoProveedorScreen(
                viewModel = viewModel,
                onNuevo = { navController.navigate("crear_tipo/0") },
                onEditar = { id -> navController.navigate("crear_tipo/$id") },
                onNavegar = { ruta -> navController.navigate(ruta) }
            )
        }
        composable(
            route = "crear_tipo/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            FormularioTipoProveedorScreen(
                viewModel = viewModel,
                idTipo = id,
                onGuardarFinalizado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }
    }
}