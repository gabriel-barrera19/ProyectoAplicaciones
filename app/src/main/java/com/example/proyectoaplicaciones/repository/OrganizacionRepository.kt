package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.local.OrganizacionDao
import com.example.proyectoaplicaciones.data.local.OrganizacionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de organizaciones.
 *
 * Actúa como intermediario entre el ViewModel
 * y el DAO de Room.
 *
 * Contiene las funciones necesarias para administrar
 * las organizaciones almacenadas en la base de datos.
 */
class OrganizacionRepository(
    private val organizacionDao: OrganizacionDao
) {

    // Obtiene la lista observable de organizaciones.
    fun obtenerTodas(): Flow<List<OrganizacionEntity>> {
        return organizacionDao.obtenerTodas()
    }

    // Busca organizaciones por nombre.
    fun buscarPorNombre(nombre: String): Flow<List<OrganizacionEntity>> {
        return organizacionDao.buscarPorNombre(nombre)
    }

    // Obtiene una organización por su identificador.
    suspend fun obtenerPorId(id: Int): OrganizacionEntity? {
        return organizacionDao.obtenerPorId(id)
    }

    // Guarda una organización nueva.
    suspend fun insertar(organizacion: OrganizacionEntity) {
        organizacionDao.insertar(organizacion)
    }

    // Actualiza una organización existente.
    suspend fun actualizar(organizacion: OrganizacionEntity) {
        organizacionDao.actualizar(organizacion)
    }

    // Elimina una organización.
    suspend fun eliminar(organizacion: OrganizacionEntity) {
        organizacionDao.eliminar(organizacion)
    }
}