package com.example.proyectoaplicaciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones disponibles para administrar
 * los contactos almacenados en Room.
 */
@Dao
interface ContactoDao {

    @Insert
    suspend fun insertar(contacto: ContactoEntity)

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ContactoEntity>>

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ContactoEntity?

    @Query("""
        SELECT * FROM contactos
        WHERE organizacionId = :organizacionId
        ORDER BY nombre ASC
    """)
    fun obtenerPorOrganizacion(
        organizacionId: Int
    ): Flow<List<ContactoEntity>>

    @Query("""
        SELECT * FROM contactos
        WHERE nombre LIKE '%' || :nombre || '%'
        ORDER BY nombre ASC
    """)
    fun buscarPorNombre(nombre: String): Flow<List<ContactoEntity>>

    @Update
    suspend fun actualizar(contacto: ContactoEntity)

    @Delete
    suspend fun eliminar(contacto: ContactoEntity)
}
