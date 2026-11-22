package com.example.proyectoaplicaciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones disponibles para administrar
 * las organizaciones almacenadas en Room.
 */
@Dao
interface OrganizacionDao {

    // Registrar una nueva organización.
    @Insert
    suspend fun insertar(organizacion: OrganizacionEntity)

    // Obtener todas las organizaciones.
    // Flow permite observar los cambios automáticamente.
    @Query("SELECT * FROM organizaciones ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<OrganizacionEntity>>

    // Buscar una organización por su identificador.
    @Query("SELECT * FROM organizaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Int): OrganizacionEntity?

    // Buscar organizaciones por nombre.
    @Query("SELECT * FROM organizaciones WHERE nombre LIKE '%' || :nombre || '%' ORDER BY nombre ASC")
    fun buscarPorNombre(nombre: String): Flow<List<OrganizacionEntity>>

    // Actualizar una organización existente.
    @Update
    suspend fun actualizar(organizacion: OrganizacionEntity)

    // Eliminar una organización.
    @Delete
    suspend fun eliminar(organizacion: OrganizacionEntity)
}