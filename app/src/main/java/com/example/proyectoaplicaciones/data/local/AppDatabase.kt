package com.example.proyectoaplicaciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Define la base de datos local de la aplicación.
 */
@Database(
    entities = [OportunidadEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Permite acceder a las operaciones de oportunidades.
    abstract fun oportunidadDao(): OportunidadDao

    companion object {

        // Conserva una única instancia de la base de datos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}