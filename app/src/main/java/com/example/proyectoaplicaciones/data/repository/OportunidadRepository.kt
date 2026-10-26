package com.example.proyectoaplicaciones.data.repository

import com.example.proyectoaplicaciones.data.local.OportunidadDao
import com.example.proyectoaplicaciones.data.local.OportunidadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de oportunidades.
 *
 * Actúa como intermediario entre el ViewModel
 * y el DAO de Room.
 *
 * Acá tenemos las funciones que nos permiten mantener la persistencia
 * de los datos de la base de datos.
 */
class OportunidadRepository(
    private val oportunidadDao: OportunidadDao
) {

    // Obtiene la lista observable de oportunidades.
    fun obtenerTodas(): Flow<List<OportunidadEntity>> {
        return oportunidadDao.obtenerTodas()
    }

    // Guarda una oportunidad nueva.
    suspend fun insertar(oportunidad: OportunidadEntity) {
        oportunidadDao.insertar(oportunidad)
    }

    // Actualiza una oportunidad existente.
    suspend fun actualizar(oportunidad: OportunidadEntity) {
        oportunidadDao.actualizar(oportunidad)
    }

    // Elimina una oportunidad.
    suspend fun eliminar(oportunidad: OportunidadEntity) {
        oportunidadDao.eliminar(oportunidad)
    }
}