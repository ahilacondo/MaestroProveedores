package com.solucionesmoviles.proveedores.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "proveedores",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Pais::class,
            parentColumns = ["id"],
            childColumns = ["paisId"],
            onDelete = androidx.room.ForeignKey.RESTRICT
        ),
        androidx.room.ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = androidx.room.ForeignKey.RESTRICT
        ),
        // NUEVA RELACIÓN
        androidx.room.ForeignKey(
            entity = TipoProveedor::class,
            parentColumns = ["id"],
            childColumns = ["tipoProveedorId"],
            onDelete = androidx.room.ForeignKey.RESTRICT
        )
    ]
)
data class Proveedor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val ruc: String,
    val tipoProveedorId: Int,
    val paisId: Int,
    val categoriaId: Int,
    val estado: String = "A"
)