package com.example.proyectoaplicaciones.model

/**
 * Representa los mensajes de error del formulario
 * de contactos.
 */
data class ContactoErrores(
    val nombre: String? = null,
    val cargo: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val organizacionId: String? = null
)