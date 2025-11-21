package com.solucionesmoviles.proveedores.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paises")
data class Pais(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String, // Ej: "PE"
    val nombre: String, // Ej: "Perú"
    val estado: String = "A" // A=Activo, I=Inactivo, *=Eliminado
)