package com.example.proyectoaplicaciones.model

// Mensajes de error asociados a los campos del formulario de eventos.
data class EventoErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val fechaInicio: String? = null,
    val fechaTermino: String? = null,
    val ubicacion: String? = null,
    val comuna: String? = null,
    val region: String? = null,
    val fechas: String? = null
)