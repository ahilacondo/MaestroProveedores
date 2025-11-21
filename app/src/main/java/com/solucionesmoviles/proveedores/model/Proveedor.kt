package com.solucionesmoviles.proveedores.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "proveedores",
    foreignKeys = [
        // Relación con PAÍS (Si borras el país, no deja borrar el proveedor)
        ForeignKey(
            entity = Pais::class,
            parentColumns = ["id"],
            childColumns = ["paisId"],
            onDelete = ForeignKey.RESTRICT
        ),
        // Relación con CATEGORÍA
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Proveedor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val ruc: String,
    val tipoProveedor: String, // "Nacional", "Internacional", etc.
    val paisId: Int,           // Aquí guardamos el ID del País (No el nombre)
    val categoriaId: Int,      // Aquí guardamos el ID de la Categoría
    val estado: String = "A"
)