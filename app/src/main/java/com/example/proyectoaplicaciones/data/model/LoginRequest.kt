package com.example.proyectoaplicaciones.data.model

// Datos que se envían al backend para iniciar sesión.
data class LoginRequest(
    val correo: String,
    val password: String
)