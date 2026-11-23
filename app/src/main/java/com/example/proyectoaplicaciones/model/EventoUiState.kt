package com.example.proyectoaplicaciones.model

// Representa el estado actual del formulario de eventos.
data class EventoUiState(

    // Información general del evento.
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",

    // Fechas y horario.
    val fechaInicio: String = "",
    val fechaTermino: String = "",
    val horario: String = "",

    // Ubicación del evento.
    val ubicacion: String = "",
    val comuna: String = "",
    val region: String = "",

    // Organización asociada.
    val organizacionId: Int? = null,

    // Información del público.
    val publicoObjetivo: String = "",
    val publicoEsperado: String = "",
    val cantidadEstimadaAsistentes: String = "",

    // Condiciones de participación.
    val condicionesParticipacion: String = "",
    val requisitos: String = "",
    val costosReferenciales: String = "",
    val espaciosDisponibles: String = "",

    // Observaciones adicionales.
    val observaciones: String = "",

    // Errores asociados a los campos.
    val errores: EventoErrores = EventoErrores(),

    // Indica si se está editando un evento existente.
    val esEdicion: Boolean = false
)