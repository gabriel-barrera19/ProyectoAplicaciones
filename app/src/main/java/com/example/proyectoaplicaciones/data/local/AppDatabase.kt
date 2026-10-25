package com.example.proyectoaplicaciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Define la base de datos local de la aplicación.
 */
@Database(
    entities = [OportunidadEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Permite acceder a las operaciones de oportunidades.
    abstract fun oportunidadDao(): OportunidadDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {

                // Agrega la fecha y asigna un valor vacío a los registros antiguos.
                database.execSQL(
                    "ALTER TABLE oportunidades ADD COLUMN fecha TEXT NOT NULL DEFAULT ''"
                )
            }
        }
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
                    // Aplica la migración sin eliminar los datos existentes.
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}