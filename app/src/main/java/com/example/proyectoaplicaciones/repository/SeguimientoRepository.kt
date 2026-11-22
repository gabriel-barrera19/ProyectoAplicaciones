package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.local.SeguimientoDao
import com.example.proyectoaplicaciones.data.local.SeguimientoEntity
import kotlinx.coroutines.flow.Flow

class SeguimientoRepository(
    private val seguimientoDao: SeguimientoDao
) {

    // Registra un nuevo seguimiento.
    suspend fun insertar(seguimiento: SeguimientoEntity) {
        seguimientoDao.insertar(seguimiento)
    }

    // Obtiene el historial de una oportunidad específica.
    fun obtenerPorOportunidad(
        oportunidadId: Int
    ): Flow<List<SeguimientoEntity>> {
        return seguimientoDao.obtenerPorOportunidad(oportunidadId)
    }

    // Elimina un seguimiento.
    suspend fun eliminar(seguimiento: SeguimientoEntity) {
        seguimientoDao.eliminar(seguimiento)
    }
}