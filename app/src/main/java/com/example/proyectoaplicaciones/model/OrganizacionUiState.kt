package com.example.proyectoaplicaciones.model

/**
 * Representa el estado actual del formulario
 * de organizaciones.
 */
data class OrganizacionUiState(

    // Nombre de la organización.
    val nombre: String = "",

    // Rubro o actividad económica.
    val rubro: String = "",

    // Tipo de organización.
    val tipo: String = "",

    // Dirección de la organización.
    val direccion: String = "",

    // Comuna donde se encuentra.
    val comuna: String = "",

    // Región donde se encuentra.
    val region: String = "",

    // Teléfono de contacto.
    val telefono: String = "",

    // Correo electrónico de la organización.
    val correo: String = "",

    // Descripción adicional.
    val descripcion: String = "",

    // Mensajes de error de los campos.
    val errores: OrganizacionErrores = OrganizacionErrores(),

    // Indica si se está editando una organización existente.
    val esEdicion: Boolean = false
)