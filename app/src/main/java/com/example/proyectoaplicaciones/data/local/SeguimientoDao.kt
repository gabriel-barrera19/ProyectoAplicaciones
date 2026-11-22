package com.example.proyectoaplicaciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    // Registra un nuevo seguimiento.
    @Insert
    suspend fun insertar(seguimiento: SeguimientoEntity)

    // Obtiene todos los seguimientos de una oportunidad.
    @Query(
        """
        SELECT * FROM seguimientos
        WHERE oportunidadId = :oportunidadId
        ORDER BY id DESC
        """
    )
    fun obtenerPorOportunidad(
        oportunidadId: Int
    ): Flow<List<SeguimientoEntity>>

    // Elimina un seguimiento.
    @Delete
    suspend fun eliminar(seguimiento: SeguimientoEntity)
}