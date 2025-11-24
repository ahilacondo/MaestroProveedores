package com.solucionesmoviles.proveedores.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tipos_proveedor")
data class TipoProveedor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String, // Ej: "TP-001"
    val nombre: String, // Ej: "Nacional", "Internacional"
    val estado: String = "A" // A=Activo, I=Inactivo, *=Eliminado
)