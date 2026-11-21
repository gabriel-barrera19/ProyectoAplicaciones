package com.example.proyectoaplicaciones.model
import com.example.proyectoaplicaciones.data.model.UsuarioResponse

// Representa el estado actual de la pantalla de Login.
data class LoginUiState(

    // Correo ingresado por el usuario.
    val usuario: String = "",

    // Contraseña ingresada.
    val contrasena: String = "",

    // Mensajes de error del formulario.
    val errores: LoginErrores = LoginErrores(),

    // Indica si se está procesando el inicio de sesión.
    val cargando: Boolean = false,

    // Datos del usuario que inició sesión correctamente.
    val usuarioAutenticado: UsuarioResponse? = null
)