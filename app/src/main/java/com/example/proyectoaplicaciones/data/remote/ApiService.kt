package com.example.proyectoaplicaciones.data.remote

import com.example.proyectoaplicaciones.data.model.UsuarioRequest
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRequest
    ): Response<UsuarioResponse>
}