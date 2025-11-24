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

@Dao
interface PaisDao {
    @Query("SELECT * FROM paises ORDER BY nombre ASC")
    fun getAll(): Flow<List<Pais>>

    @Query("SELECT * FROM paises WHERE estado = 'A' ORDER BY nombre ASC")
    fun getActivos(): Flow<List<Pais>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pais: Pais)

    @Update
    suspend fun update(pais: Pais)

    @Query("SELECT * FROM paises WHERE id = :id")
    suspend fun getById(id: Int): Pais?

    // NUEVO: Verifica duplicados (excluyendo el propio ID si estamos editando)
    @Query("SELECT COUNT(*) FROM paises WHERE nombre = :nombre AND id != :id")
    suspend fun existeNombre(nombre: String, id: Int): Int
}

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

    // NUEVO: Verifica duplicados
    @Query("SELECT COUNT(*) FROM categorias WHERE nombre = :nombre AND id != :id")
    suspend fun existeNombre(nombre: String, id: Int): Int
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

    // NUEVO: Verifica duplicados
    @Query("SELECT COUNT(*) FROM tipos_proveedor WHERE nombre = :nombre AND id != :id")
    suspend fun existeNombre(nombre: String, id: Int): Int
}

@Dao
interface ProveedorDao {
    @Query("SELECT * FROM proveedores WHERE nombre LIKE '%' || :query || '%' OR ruc LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscarProveedores(query: String): Flow<List<Proveedor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(proveedor: Proveedor)

    @Update
    suspend fun update(proveedor: Proveedor)

    @Query("SELECT * FROM proveedores WHERE id = :id")
    suspend fun getById(id: Int): Proveedor?

    // ACTUALIZADO: Validar RUC excluyendo el propio ID (para edición)
    @Query("SELECT COUNT(*) FROM proveedores WHERE ruc = :ruc AND id != :id")
    suspend fun existeRuc(ruc: String, id: Int): Int

    // Validar si un RUC existe (versión simple para nuevo registro, opcional si usas la de arriba pasando 0)
    // Pero la de arriba con id=0 funciona igual para nuevos.

    @Query("SELECT COUNT(*) FROM proveedores WHERE paisId = :paisId AND estado != '*'")
    suspend fun contarPorPais(paisId: Int): Int

    @Query("SELECT COUNT(*) FROM proveedores WHERE categoriaId = :catId AND estado != '*'")
    suspend fun contarPorCategoria(catId: Int): Int

    // Validación para Tipos
    @Query("SELECT COUNT(*) FROM proveedores WHERE tipoProveedorId = :tipoId AND estado != '*'")
    suspend fun contarPorTipo(tipoId: Int): Int
}