package com.solucionesmoviles.proveedores.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solucionesmoviles.proveedores.model.Categoria
import com.solucionesmoviles.proveedores.model.Pais
import com.solucionesmoviles.proveedores.model.Proveedor
import com.solucionesmoviles.proveedores.model.TipoProveedor

@Database(
    entities = [Proveedor::class, Pais::class, Categoria::class, TipoProveedor::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun proveedorDao(): ProveedorDao
    abstract fun paisDao(): PaisDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun tipoProveedorDao(): TipoProveedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proveedores_db" // Nombre del archivo físico en el celular
                )
                    // Esto destruye la BD si cambias las tablas (útil en desarrollo)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}