package com.example.proyectoaplicaciones.repository

import com.example.proyectoaplicaciones.data.model.LoginRequest
import com.example.proyectoaplicaciones.data.model.UsuarioRequest
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import com.example.proyectoaplicaciones.data.remote.ApiService
import retrofit2.Response

/**
 * Repositorio encargado de las operaciones
 * relacionadas con los usuarios.
 */
class UsuarioRepository(
    private val apiService: ApiService
) {

    // Autentica un usuario utilizando el backend.
    suspend fun iniciarSesion(
        solicitud: LoginRequest
    ): Response<UsuarioResponse> {

        return apiService.iniciarSesion(solicitud)
    }

    // Registra un usuario en el backend.
    suspend fun registrarUsuario(
        usuario: UsuarioRequest
    ): Response<UsuarioResponse> {

        return apiService.registrarUsuario(usuario)
    }
}