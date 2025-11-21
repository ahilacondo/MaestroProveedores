package com.solucionesmoviles.proveedores.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String, // Ej: "CAT-001"
    val nombre: String, // Ej: "Lácteos"
    val estado: String = "A"
)