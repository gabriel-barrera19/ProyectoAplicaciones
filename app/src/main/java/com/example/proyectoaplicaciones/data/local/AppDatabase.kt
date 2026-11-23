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
        SeguimientoEntity::class,
        EventoEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao

    abstract fun organizacionDao(): OrganizacionDao

    abstract fun contactoDao(): ContactoDao

    abstract fun seguimientoDao(): SeguimientoDao

    abstract fun eventoDao(): EventoDao

    companion object {

        // Migración de la versión 1 a la versión 2.
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE oportunidades
                    ADD COLUMN fecha TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

        // Migración de la versión 2 a la versión 3.
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

        // Migración de la versión 4 a la versión 5.
        private val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(database: SupportSQLiteDatabase) {

                // Crear la tabla de eventos.
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS eventos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        fechaInicio TEXT NOT NULL,
                        fechaTermino TEXT NOT NULL,
                        horario TEXT NOT NULL,
                        ubicacion TEXT NOT NULL,
                        comuna TEXT NOT NULL,
                        region TEXT NOT NULL,
                        organizacionId INTEGER,
                        publicoObjetivo TEXT NOT NULL,
                        publicoEsperado TEXT NOT NULL,
                        cantidadEstimadaAsistentes INTEGER,
                        condicionesParticipacion TEXT NOT NULL,
                        requisitos TEXT NOT NULL,
                        costosReferenciales TEXT NOT NULL,
                        espaciosDisponibles TEXT NOT NULL,
                        observaciones TEXT NOT NULL,
                        FOREIGN KEY(organizacionId)
                        REFERENCES organizaciones(id)
                        ON UPDATE NO ACTION
                        ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                // Índice para consultar eventos por organización.
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_eventos_organizacionId
                    ON eventos(organizacionId)
                    """.trimIndent()
                )

                // Agregar la relación opcional entre oportunidad y evento.
                database.execSQL(
                    """
                    ALTER TABLE oportunidades
                    ADD COLUMN eventoId INTEGER
                    """.trimIndent()
                )

                // Índice para consultar oportunidades por evento.
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_oportunidades_eventoId
                    ON oportunidades(eventoId)
                    """.trimIndent()
                )
            }
        }

        // Migración de la versión 5 a la versión 6.
        // Corrige la clave foránea de oportunidades hacia eventos
        // y elimina el valor predeterminado de fecha.
        // Conserva los seguimientos existentes.
        private val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(database: SupportSQLiteDatabase) {

                // 1. Guardar temporalmente los seguimientos.
                // Se hace antes de reconstruir oportunidades porque
                // seguimientos tiene una clave foránea hacia esa tabla.
                database.execSQL(
                    """
                    CREATE TABLE seguimientos_backup AS
                    SELECT
                        id,
                        oportunidadId,
                        fecha,
                        tipoGestion,
                        resultado,
                        observaciones
                    FROM seguimientos
                    """.trimIndent()
                )

                // 2. Eliminar la tabla de seguimientos original.
                database.execSQL(
                    "DROP TABLE seguimientos"
                )

                // 3. Crear la nueva estructura de oportunidades.
                database.execSQL(
                    """
                    CREATE TABLE oportunidades_nueva (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        fuente TEXT NOT NULL,
                        prioridad TEXT NOT NULL,
                        potencialComercial TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        observaciones TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        eventoId INTEGER,
                        FOREIGN KEY(eventoId)
                        REFERENCES eventos(id)
                        ON UPDATE NO ACTION
                        ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                // 4. Copiar todas las oportunidades existentes.
                database.execSQL(
                    """
                    INSERT INTO oportunidades_nueva (
                        id,
                        nombre,
                        descripcion,
                        tipo,
                        fuente,
                        prioridad,
                        potencialComercial,
                        estado,
                        observaciones,
                        fecha,
                        eventoId
                    )
                    SELECT
                        id,
                        nombre,
                        descripcion,
                        tipo,
                        fuente,
                        prioridad,
                        potencialComercial,
                        estado,
                        observaciones,
                        fecha,
                        eventoId
                    FROM oportunidades
                    """.trimIndent()
                )

                // 5. Reemplazar la tabla anterior.
                database.execSQL(
                    "DROP TABLE oportunidades"
                )

                database.execSQL(
                    """
                    ALTER TABLE oportunidades_nueva
                    RENAME TO oportunidades
                    """.trimIndent()
                )

                // 6. Crear nuevamente el índice de eventoId.
                database.execSQL(
                    """
                    CREATE INDEX index_oportunidades_eventoId
                    ON oportunidades(eventoId)
                    """.trimIndent()
                )

                // 7. Recrear la tabla de seguimientos con su relación.
                database.execSQL(
                    """
                    CREATE TABLE seguimientos (
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

                // 8. Restaurar todos los seguimientos.
                database.execSQL(
                    """
                    INSERT INTO seguimientos (
                        id,
                        oportunidadId,
                        fecha,
                        tipoGestion,
                        resultado,
                        observaciones
                    )
                    SELECT
                        id,
                        oportunidadId,
                        fecha,
                        tipoGestion,
                        resultado,
                        observaciones
                    FROM seguimientos_backup
                    """.trimIndent()
                )

                // 9. Recrear el índice de seguimientos.
                database.execSQL(
                    """
                    CREATE INDEX index_seguimientos_oportunidadId
                    ON seguimientos(oportunidadId)
                    """.trimIndent()
                )

                // 10. Eliminar la copia temporal.
                database.execSQL(
                    "DROP TABLE seguimientos_backup"
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
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}