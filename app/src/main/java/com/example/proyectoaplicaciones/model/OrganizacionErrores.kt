package com.example.proyectoaplicaciones.model

/**
 * Representa los mensajes de error del formulario
 * de organizaciones.
 *
 * Cada propiedad almacena un mensaje cuando existe
 * un dato incorrecto o un campo obligatorio vacío.
 */
data class OrganizacionErrores(
    val nombre: String? = null,
    val rubro: String? = null,
    val tipo: String? = null,
    val direccion: String? = null,
    val comuna: String? = null,
    val region: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val descripcion: String? = null
)