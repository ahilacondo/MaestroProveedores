package com.solucionesmoviles.proveedores.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solucionesmoviles.proveedores.model.Categoria
import com.solucionesmoviles.proveedores.model.Pais
import com.solucionesmoviles.proveedores.model.Proveedor
import com.solucionesmoviles.proveedores.model.TipoProveedor
import kotlinx.coroutines.flow.Flow

// 1. DAO PARA PAÍSES
@Dao
interface PaisDao {
    // Lista completa para el mantenimiento (Modo Admin)
    @Query("SELECT * FROM paises ORDER BY nombre ASC")
    fun getAll(): Flow<List<Pais>>

    // Lista filtrada para el selector (Solo Activos "A") - Requisito de la guía
    @Query("SELECT * FROM paises WHERE estado = 'A' ORDER BY nombre ASC")
    fun getActivos(): Flow<List<Pais>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pais: Pais)

    @Update
    suspend fun update(pais: Pais)

    @Query("SELECT * FROM paises WHERE id = :id")
    suspend fun getById(id: Int): Pais?
}

// 2. DAO PARA CATEGORÍAS
@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE estado = 'A' ORDER BY nombre ASC")
    fun getActivos(): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)

    @Update
    suspend fun update(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getById(id: Int): Categoria?
}

// 3. DAO PARA PROVEEDORES (MAESTRO)
@Dao
interface ProveedorDao {
    // Listado principal con búsqueda (Filtrando por nombre o RUC)
    @Query("SELECT * FROM proveedores WHERE nombre LIKE '%' || :query || '%' OR ruc LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscarProveedores(query: String): Flow<List<Proveedor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(proveedor: Proveedor)

    @Update
    suspend fun update(proveedor: Proveedor)

    @Query("SELECT * FROM proveedores WHERE id = :id")
    suspend fun getById(id: Int): Proveedor?

    // Validación: Verificar si ya existe un RUC (Regla de negocio)
    @Query("SELECT COUNT(*) FROM proveedores WHERE ruc = :ruc")
    suspend fun existeRuc(ruc: String): Int

    // Validar si un país está siendo usado por algún proveedor activo o inactivo (no eliminado)
    @Query("SELECT COUNT(*) FROM proveedores WHERE paisId = :paisId AND estado != '*'")
    suspend fun contarPorPais(paisId: Int): Int

    // Validar si una categoría está siendo usada
    @Query("SELECT COUNT(*) FROM proveedores WHERE categoriaId = :catId AND estado != '*'")
    suspend fun contarPorCategoria(catId: Int): Int
}

@Dao
interface TipoProveedorDao {
    @Query("SELECT * FROM tipos_proveedor ORDER BY nombre ASC")
    fun getAll(): Flow<List<TipoProveedor>>

    @Query("SELECT * FROM tipos_proveedor WHERE estado = 'A' ORDER BY nombre ASC")
    fun getActivos(): Flow<List<TipoProveedor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tipo: TipoProveedor)

    @Update
    suspend fun update(tipo: TipoProveedor)

    @Query("SELECT * FROM tipos_proveedor WHERE id = :id")
    suspend fun getById(id: Int): TipoProveedor?
}