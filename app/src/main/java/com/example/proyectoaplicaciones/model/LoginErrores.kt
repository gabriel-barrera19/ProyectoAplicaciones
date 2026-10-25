package com.example.proyectoaplicaciones.model

// Contiene los errores de validación del formulario de Login.
data class LoginErrores(

    // Error del campo usuario.
    val usuario: String? = null,

    // Error del campo contraseña.
    val contrasena: String? = null,

    // Error general cuando las credenciales no coinciden.
    val credenciales: String? = null
)