package com.example.proyectoaplicaciones.model

// Representa el estado actual del formulario de oportunidades.
data class OportunidadUiState(

    // Nombre de la oportunidad comercial.
    val nombre: String = "",

    // Descripción de la oportunidad.
    val descripcion: String = "",

    // Tipo de oportunidad seleccionada.
    val tipo: String = "",

    // Prioridad asignada a la oportunidad.
    val prioridad: String = "",

    // Potencial comercial de la oportunidad.
    val potencialComercial: String = "",

    // Estado inicial indicado en los lineamientos.
    val estado: String = "Evento identificado",

    // Fuente desde la que se obtuvo la oportunidad.
    val fuente: String = "",

    // Observaciones adicionales.
    val observaciones: String = "",

    // Fecha asociada a la oportunidad, inicialmente vacía.
    val fecha: String = "",

    // Mensajes de error asociados a los campos.
    val errores: OportunidadErrores = OportunidadErrores(),

    // Indica si el formulario está editando una oportunidad existente.
    val esEdicion: Boolean = false
)
