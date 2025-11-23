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

    // --- PREFERENCIAS DE TEMA ---
    private val userPreferences = UserPreferencesRepository(application)

    val isDarkMode = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.saveDarkMode(isDark)
        }
    }

    // --- VARIABLES TEMPORALES PARA EL FORMULARIO (Persistencia) ---
    var nombreFormulario by mutableStateOf("")
    var rucFormulario by mutableStateOf("")
    var tipoFormulario by mutableStateOf("Nacional")

    var formularioCargadoId by mutableStateOf<Int?>(null)

    fun limpiarFormulario() {
        nombreFormulario = ""
        rucFormulario = ""
        tipoFormulario = "Nacional"
        formularioCargadoId = null
    }

    fun cargarDatosParaEdicion(id: Int, proveedor: Proveedor) {
        if (formularioCargadoId != id) {
            nombreFormulario = proveedor.nombre
            rucFormulario = proveedor.ruc
            tipoFormulario = proveedor.tipoProveedor
            formularioCargadoId = id
        }
    }

    // 2. Estados de la UI (Buscador y Ordenamiento Proveedores)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _ordenarPorNombre = MutableStateFlow(true)
    val ordenarPorNombre = _ordenarPorNombre.asStateFlow()

    // 3. LISTA MAESTRA PROVEEDORES (Eliminados al final)
    @OptIn(ExperimentalCoroutinesApi::class)
    val listaProveedores: Flow<List<Proveedor>> = combine(
        _searchQuery,
        _ordenarPorNombre
    ) { query, porNombre ->
        Pair(query, porNombre)
    }.flatMapLatest { (query, porNombre) ->
        proveedorDao.buscarProveedores(query).map { lista ->
            val listaOrdenada = if (porNombre) {
                lista.sortedBy { it.nombre }
            } else {
                lista.sortedBy { it.ruc }
            }
            // Eliminados (*) al final
            listaOrdenada.sortedBy { it.estado == "*" }
        }
    }

    // 4. LISTAS AUXILIARES

    // Selectores (Solo Activos "A") - No requieren cambios, la BD ya filtra
    val listaPaisesActivos = paisDao.getActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaCategoriasActivas = categoriaDao.getActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- CORRECCIÓN AQUÍ ---
    // Listas completas para Mantenimientos (Ahora con Eliminados al final)

    val listaPaisesTodos = paisDao.getAll()
        .map { lista ->
            // Ordenamos para que los eliminados (*) vayan al fondo
            lista.sortedBy { it.estado == "*" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaCategoriasTodas = categoriaDao.getAll()
        .map { lista ->
            // Ordenamos para que los eliminados (*) vayan al fondo
            lista.sortedBy { it.estado == "*" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 5. FUNCIONES DE CONTROL DE UI
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun cambiarOrden() {
        _ordenarPorNombre.value = !_ordenarPorNombre.value
    }

    // 6. OPERACIONES CRUD
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

    // VALIDACIONES (Regla 2)
    suspend fun puedeInactivarPais(id: Int): Boolean {
        return proveedorDao.contarPorPais(id) == 0
    }

    suspend fun puedeInactivarCategoria(id: Int): Boolean {
        return proveedorDao.contarPorCategoria(id) == 0
    }


    // --- PAÍSES ---
    fun guardarPais(pais: Pais) {
        viewModelScope.launch {
            if (pais.id == 0) {
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


    // --- CATEGORÍAS ---
    fun guardarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            if (categoria.id == 0) {
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
}