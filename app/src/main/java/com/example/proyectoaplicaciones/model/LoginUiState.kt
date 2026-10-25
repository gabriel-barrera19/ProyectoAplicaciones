package com.example.proyectoaplicaciones.model

// Representa el estado actual de la pantalla de Login.
data class LoginUiState(

    // Usuario ingresado.
    val usuario: String = "",

    // Contraseña ingresada.
    val contrasena: String = "",

    // Mensajes de error agrupados en un objeto independiente.
    val errores: LoginErrores = LoginErrores()
)