package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.LoginRequest
import com.example.proyectoaplicaciones.data.model.UsuarioRequest
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Autenticación de usuarios.
    @POST("api/auth/login")
    suspend fun iniciarSesion(
        @Body solicitud: LoginRequest
    ): Response<UsuarioResponse>

    // Registro de usuarios.
    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRequest
    ): Response<UsuarioResponse>
}