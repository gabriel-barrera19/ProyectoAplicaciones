package com.example.proyectoaplicaciones.model

/**
 * Representa el estado actual del formulario de contactos.
 * Mantiene los datos ingresados, los errores y el modo
 * de edición o creación.
 */
data class ContactoUiState(
    val nombre: String = "",
    val cargo: String = "",
    val correo: String = "",
    val telefono: String = "",
    val organizacionId: Int? = null,
    val errores: ContactoErrores = ContactoErrores(),
    val esEdicion: Boolean = false
)