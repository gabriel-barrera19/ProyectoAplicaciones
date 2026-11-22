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
        ContactoEntity::class,
        SeguimientoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao

    abstract fun organizacionDao(): OrganizacionDao

    abstract fun contactoDao(): ContactoDao

    abstract fun seguimientoDao(): SeguimientoDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE oportunidades ADD COLUMN fecha TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

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

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_contactos_organizacionId
                    ON contactos(organizacionId)
                    """.trimIndent()
                )
            }
        }

        // Migración de la versión 3 a la versión 4.
        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS seguimientos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        oportunidadId INTEGER NOT NULL,
                        fecha TEXT NOT NULL,
                        tipoGestion TEXT NOT NULL,
                        resultado TEXT NOT NULL,
                        observaciones TEXT NOT NULL,
                        FOREIGN KEY(oportunidadId)
                        REFERENCES oportunidades(id)
                        ON UPDATE NO ACTION
                        ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_seguimientos_oportunidadId
                    ON seguimientos(oportunidadId)
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}