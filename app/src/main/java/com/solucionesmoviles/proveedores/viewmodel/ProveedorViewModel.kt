package com.solucionesmoviles.proveedores.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.solucionesmoviles.proveedores.data.AppDatabase
import com.solucionesmoviles.proveedores.data.UserPreferencesRepository
import com.solucionesmoviles.proveedores.model.Categoria
import com.solucionesmoviles.proveedores.model.Pais
import com.solucionesmoviles.proveedores.model.Proveedor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProveedorViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Inicialización de la Base de Datos y DAOs
    private val db = AppDatabase.getDatabase(application)
    private val proveedorDao = db.proveedorDao()
    private val paisDao = db.paisDao()
    private val categoriaDao = db.categoriaDao()

    // --- NUEVO: PREFERENCIAS DE TEMA ---
    private val userPreferences = UserPreferencesRepository(application)

    // Estado del tema que observa la pantalla de Ajustes
    val isDarkMode = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Función para cambiar el tema
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.saveDarkMode(isDark)
        }
    }

    // --- NUEVO: VARIABLES TEMPORALES PARA EL FORMULARIO (Persistencia al navegar) ---
    // Usamos mutableStateOf de Compose para que la UI se actualice sola
    var nombreFormulario by androidx.compose.runtime.mutableStateOf("")
    var rucFormulario by androidx.compose.runtime.mutableStateOf("")
    var tipoFormulario by androidx.compose.runtime.mutableStateOf("Nacional")

    // Control para saber si ya cargamos los datos de un ID específico (evita recargas innecesarias)
    var formularioCargadoId by androidx.compose.runtime.mutableStateOf<Int?>(null)

    // Función para limpiar (Cuando das click a "Nuevo")
    fun limpiarFormulario() {
        nombreFormulario = ""
        rucFormulario = ""
        tipoFormulario = "Nacional"
        formularioCargadoId = null
    }

    // Función para cargar datos de edición (Solo si no están cargados ya)
    fun cargarDatosParaEdicion(id: Int, proveedor: Proveedor) {
        if (formularioCargadoId != id) {
            nombreFormulario = proveedor.nombre
            rucFormulario = proveedor.ruc
            tipoFormulario = proveedor.tipoProveedor
            formularioCargadoId = id
        }
    }

    // --------------------------------------------------
    // 2. Estados de la UI (Buscador y Ordenamiento)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _ordenarPorNombre = MutableStateFlow(true) // true = Nombre, false = RUC
    val ordenarPorNombre = _ordenarPorNombre.asStateFlow()

    // 3. LISTA MAESTRA DE PROVEEDORES (Reactiva a búsqueda y orden)
    @OptIn(ExperimentalCoroutinesApi::class)
    val listaProveedores: Flow<List<Proveedor>> = combine(
        _searchQuery,
        _ordenarPorNombre
    ) { query, porNombre ->
        Pair(query, porNombre)
    }.flatMapLatest { (query, porNombre) ->
        // A. Buscamos en BD según el texto
        proveedorDao.buscarProveedores(query).map { lista ->
            // 1. Primero ordenamos por Nombre o RUC (según lo que elija el usuario)
            val listaOrdenada = if (porNombre) {
                lista.sortedBy { it.nombre }
            } else {
                lista.sortedBy { it.ruc }
            }

            // 2. LUEGO volvemos a ordenar para mandar los eliminados (*) al final.
            // false va antes que true, por lo tanto los activos van antes que los eliminados
            listaOrdenada.sortedBy { it.estado == "*" }
        }
    }

    // 4. LISTAS AUXILIARES (PAÍSES Y CATEGORÍAS)

    // Listas filtradas (Solo Activos) para los Selectores
    val listaPaisesActivos = paisDao.getActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaCategoriasActivas = categoriaDao.getActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Listas completas (Todos los estados) para los Mantenimientos
    val listaPaisesTodos = paisDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaCategoriasTodas = categoriaDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 5. FUNCIONES DE CONTROL DE UI
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun cambiarOrden() {
        _ordenarPorNombre.value = !_ordenarPorNombre.value
    }


    // 6. OPERACIONES CRUD (Create, Read, Update, Delete)

    // --- A. PROVEEDORES ---
    fun guardarProveedor(proveedor: Proveedor) {
        viewModelScope.launch {
            if (proveedor.id == 0) {
                proveedorDao.insert(proveedor)
            } else {
                proveedorDao.update(proveedor)
            }
        }
    }

    fun inactivarProveedor(proveedor: Proveedor) {
        viewModelScope.launch { proveedorDao.update(proveedor.copy(estado = "I")) }
    }

    fun reactivarProveedor(proveedor: Proveedor) {
        viewModelScope.launch { proveedorDao.update(proveedor.copy(estado = "A")) }
    }

    fun eliminarProveedor(proveedor: Proveedor) {
        viewModelScope.launch { proveedorDao.update(proveedor.copy(estado = "*")) }
    }

    suspend fun getProveedorById(id: Int): Proveedor? = proveedorDao.getById(id)


    // --- B. PAÍSES (Con Código Automático) ---
    fun guardarPais(pais: Pais) {
        viewModelScope.launch {
            if (pais.id == 0) {
                // Generamos código único: Ej "PAIS-4812"
                val codigoAuto = "PAIS-${System.currentTimeMillis().toString().takeLast(4)}"
                paisDao.insert(pais.copy(codigo = codigoAuto))
            } else {
                paisDao.update(pais)
            }
        }
    }

    fun inactivarPais(pais: Pais) {
        viewModelScope.launch { paisDao.update(pais.copy(estado = "I")) }
    }

    fun reactivarPais(pais: Pais) {
        viewModelScope.launch { paisDao.update(pais.copy(estado = "A")) }
    }

    fun eliminarPais(pais: Pais) {
        viewModelScope.launch { paisDao.update(pais.copy(estado = "*")) }
    }

    suspend fun getPaisById(id: Int): Pais? = paisDao.getById(id)


    // --- C. CATEGORÍAS (Con Código Automático) ---
    fun guardarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            if (categoria.id == 0) {
                // Generamos código único: Ej "CAT-9021"
                val codigoAuto = "CAT-${System.currentTimeMillis().toString().takeLast(4)}"
                categoriaDao.insert(categoria.copy(codigo = codigoAuto))
            } else {
                categoriaDao.update(categoria)
            }
        }
    }

    fun inactivarCategoria(categoria: Categoria) {
        viewModelScope.launch { categoriaDao.update(categoria.copy(estado = "I")) }
    }

    fun reactivarCategoria(categoria: Categoria) {
        viewModelScope.launch { categoriaDao.update(categoria.copy(estado = "A")) }
    }

    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch { categoriaDao.update(categoria.copy(estado = "*")) }
    }

    suspend fun getCategoriaById(id: Int): Categoria? = categoriaDao.getById(id)

    // VALIDACIONES DE INTEGRIDAD
    suspend fun puedeInactivarPais(id: Int): Boolean {
        return proveedorDao.contarPorPais(id) == 0
    }

    suspend fun puedeInactivarCategoria(id: Int): Boolean {
        return proveedorDao.contarPorCategoria(id) == 0
    }
}