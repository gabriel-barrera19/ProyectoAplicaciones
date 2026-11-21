package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.UsuarioRequest
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import com.example.proyectoaplicaciones.data.remote.ApiService
import retrofit2.Response

/**
 * Repositorio de usuarios.
 *
 * Actúa como intermediario entre el ViewModel
 * y la API REST.
 *
 * Aquí tenemos las funciones que nos permiten
 * comunicarnos con el backend para gestionar usuarios.
 */
class UsuarioRepository(
    private val apiService: ApiService
) {

    // Registra un usuario en el backend.
    suspend fun registrarUsuario(
        usuario: UsuarioRequest
    ): Response<UsuarioResponse> {

        return apiService.registrarUsuario(usuario)
    }
}