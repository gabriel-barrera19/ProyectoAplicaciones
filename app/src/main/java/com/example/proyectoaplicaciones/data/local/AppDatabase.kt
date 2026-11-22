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
    entities = [
        OportunidadEntity::class,
        OrganizacionEntity::class,
        ContactoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Acceso a las operaciones de oportunidades.
    abstract fun oportunidadDao(): OportunidadDao

    // Acceso a las operaciones de organizaciones.
    abstract fun organizacionDao(): OrganizacionDao

    // Acceso a las operaciones de contactos.
    abstract fun contactoDao(): ContactoDao

    companion object {

        // Migración de la versión 1 a la versión 2.
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    "ALTER TABLE oportunidades ADD COLUMN fecha TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // Migración de la versión 2 a la versión 3.
        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {

                // Crea la tabla de organizaciones.
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS organizaciones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        rubro TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        direccion TEXT NOT NULL,
                        comuna TEXT NOT NULL,
                        region TEXT NOT NULL,
                        telefono TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        descripcion TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                // Crea la tabla de contactos.
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS contactos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        organizacionId INTEGER NOT NULL,
                        nombre TEXT NOT NULL,
                        cargo TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        telefono TEXT NOT NULL,
                        FOREIGN KEY(organizacionId)
                        REFERENCES organizaciones(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                // Crea un índice para buscar contactos por organización.
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_contactos_organizacionId
                    ON contactos(organizacionId)
                    """.trimIndent()
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
                    // Aplica las migraciones sin eliminar los datos existentes.
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}