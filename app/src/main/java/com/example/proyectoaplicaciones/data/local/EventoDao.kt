package com.example.proyectoaplicaciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Operaciones de acceso a la tabla de eventos.
 */
@Dao
interface EventoDao {

    // Registrar un evento.
    @Insert
    suspend fun insertar(evento: EventoEntity): Long

    // Consultar todos los eventos.
    @Query("SELECT * FROM eventos ORDER BY fechaInicio ASC")
    fun obtenerTodos(): Flow<List<EventoEntity>>

    // Consultar un evento por su identificador.
    @Query("SELECT * FROM eventos WHERE id = :eventoId LIMIT 1")
    suspend fun obtenerPorId(eventoId: Int): EventoEntity?

    // Consultar eventos de una organización.
    @Query(
        "SELECT * FROM eventos " +
                "WHERE organizacionId = :organizacionId " +
                "ORDER BY fechaInicio ASC"
    )
    fun obtenerPorOrganizacion(
        organizacionId: Int
    ): Flow<List<EventoEntity>>

    // Actualizar un evento.
    @Update
    suspend fun actualizar(evento: EventoEntity)

    // Eliminar un evento.
    @Delete
    suspend fun eliminar(evento: EventoEntity)
}