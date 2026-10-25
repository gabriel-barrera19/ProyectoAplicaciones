package com.example.proyectoaplicaciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones disponibles para la tabla oportunidades.
 */
@Dao
interface OportunidadDao {

    // Inserta una oportunidad nueva.
    @Insert
    suspend fun insertar(oportunidad: OportunidadEntity)

    // Observa todas las oportunidades guardadas.
    // Flow permite recibir cambios automáticamente.
    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    // Actualiza una oportunidad existente.
    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    // Elimina una oportunidad existente.
    @Delete
    suspend fun eliminar(oportunidad: OportunidadEntity)
}