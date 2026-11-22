package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.local.ContactoDao
import com.example.proyectoaplicaciones.data.local.ContactoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de contactos.
 *
 * Actúa como intermediario entre el ViewModel
 * y el DAO de Room.
 *
 * Contiene las funciones necesarias para administrar
 * los contactos almacenados en la base de datos.
 */
class ContactoRepository(
    private val contactoDao: ContactoDao
) {

    // Obtiene la lista observable de contactos.
    fun obtenerTodas(): Flow<List<ContactoEntity>> {
        return contactoDao.obtenerTodos()
    }

    // Obtiene los contactos asociados a una organización.
    fun obtenerPorOrganizacion(
        organizacionId: Int
    ): Flow<List<ContactoEntity>> {
        return contactoDao.obtenerPorOrganizacion(organizacionId)
    }

    // Busca contactos por nombre.
    fun buscarPorNombre(nombre: String): Flow<List<ContactoEntity>> {
        return contactoDao.buscarPorNombre(nombre)
    }

    // Obtiene un contacto por su identificador.
    suspend fun obtenerPorId(id: Int): ContactoEntity? {
        return contactoDao.obtenerPorId(id)
    }

    // Guarda un contacto nuevo.
    suspend fun insertar(contacto: ContactoEntity) {
        contactoDao.insertar(contacto)
    }

    // Actualiza un contacto existente.
    suspend fun actualizar(contacto: ContactoEntity) {
        contactoDao.actualizar(contacto)
    }

    // Elimina un contacto.
    suspend fun eliminar(contacto: ContactoEntity) {
        contactoDao.eliminar(contacto)
    }
}