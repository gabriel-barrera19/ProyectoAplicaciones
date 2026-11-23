package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.local.EventoDao
import com.example.proyectoaplicaciones.data.local.EventoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de eventos.
 *
 * Actúa como intermediario entre el ViewModel
 * y el DAO de Room.
 */
class EventoRepository(
    private val eventoDao: EventoDao
) {

    // Obtiene todos los eventos almacenados.
    fun obtenerTodos(): Flow<List<EventoEntity>> {
        return eventoDao.obtenerTodos()
    }

    // Busca un evento por su identificador.
    suspend fun obtenerPorId(id: Int): EventoEntity? {
        return eventoDao.obtenerPorId(id)
    }

    // Obtiene los eventos asociados a una organización.
    fun obtenerPorOrganizacion(
        organizacionId: Int
    ): Flow<List<EventoEntity>> {
        return eventoDao.obtenerPorOrganizacion(organizacionId)
    }

    // Registra un evento nuevo.
    suspend fun insertar(evento: EventoEntity): Long {
        return eventoDao.insertar(evento)
    }

    // Actualiza un evento existente.
    suspend fun actualizar(evento: EventoEntity) {
        eventoDao.actualizar(evento)
    }

    // Elimina un evento.
    suspend fun eliminar(evento: EventoEntity) {
        eventoDao.eliminar(evento)
    }
}