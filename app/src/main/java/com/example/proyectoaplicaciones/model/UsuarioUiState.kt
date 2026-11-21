package com.example.proyectoaplicaciones.model

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val rol: String = "EJECUTIVO",
    val errores: UsuarioErrores = UsuarioErrores(),
    val cargando: Boolean = false,
    val registroExitoso: Boolean = false
)