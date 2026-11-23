package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.EventoEntity
import com.example.proyectoaplicaciones.model.EventoErrores
import com.example.proyectoaplicaciones.model.EventoUiState
import com.example.proyectoaplicaciones.repository.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del módulo de eventos.
 * Gestiona el formulario, las validaciones y las operaciones CRUD.
 */
class EventoViewModel(
    private val repository: EventoRepository
) : ViewModel() {

    // Estado privado del formulario.
    private val _estado = MutableStateFlow(EventoUiState())

    // Estado público observado por la interfaz.
    val estado: StateFlow<EventoUiState> = _estado.asStateFlow()

    // Lista observable de eventos almacenados en Room.
    val eventos: StateFlow<List<EventoEntity>> =
        repository.obtenerTodos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mensajes para informar el resultado de las operaciones.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // ID del evento que se está editando.
    private var idEventoEditando: Int? = null

    // Actualiza el nombre y limpia su error.
    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null)
            )
        }
    }

    fun onTipoChange(valor: String) {
        _estado.update {
            it.copy(
                tipo = valor,
                errores = it.errores.copy(tipo = null)
            )
        }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onFechaInicioChange(valor: String) {
        _estado.update {
            it.copy(
                fechaInicio = valor,
                errores = it.errores.copy(
                    fechaInicio = null,
                    fechas = null
                )
            )
        }
    }

    fun onFechaTerminoChange(valor: String) {
        _estado.update {
            it.copy(
                fechaTermino = valor,
                errores = it.errores.copy(
                    fechaTermino = null,
                    fechas = null
                )
            )
        }
    }

    fun onHorarioChange(valor: String) {
        _estado.update { it.copy(horario = valor) }
    }

    fun onUbicacionChange(valor: String) {
        _estado.update {
            it.copy(
                ubicacion = valor,
                errores = it.errores.copy(ubicacion = null)
            )
        }
    }

    fun onComunaChange(valor: String) {
        _estado.update {
            it.copy(
                comuna = valor,
                errores = it.errores.copy(comuna = null)
            )
        }
    }

    fun onRegionChange(valor: String) {
        _estado.update {
            it.copy(
                region = valor,
                errores = it.errores.copy(region = null)
            )
        }
    }

    fun onOrganizacionChange(valor: Int?) {
        _estado.update { it.copy(organizacionId = valor) }
    }

    fun onPublicoObjetivoChange(valor: String) {
        _estado.update { it.copy(publicoObjetivo = valor) }
    }

    fun onPublicoEsperadoChange(valor: String) {
        _estado.update { it.copy(publicoEsperado = valor) }
    }

    fun onCantidadAsistentesChange(valor: String) {
        _estado.update {
            it.copy(cantidadEstimadaAsistentes = valor.filter(Char::isDigit))
        }
    }

    fun onCondicionesChange(valor: String) {
        _estado.update { it.copy(condicionesParticipacion = valor) }
    }

    fun onRequisitosChange(valor: String) {
        _estado.update { it.copy(requisitos = valor) }
    }

    fun onCostosChange(valor: String) {
        _estado.update { it.copy(costosReferenciales = valor) }
    }

    fun onEspaciosChange(valor: String) {
        _estado.update { it.copy(espaciosDisponibles = valor) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor) }
    }

    // Prepara el formulario para registrar un evento nuevo.
    fun prepararNuevoEvento() {
        idEventoEditando = null
        _estado.value = EventoUiState()
    }

    // Carga un evento existente para editarlo.
    fun iniciarEdicion(evento: EventoEntity) {
        idEventoEditando = evento.id

        _estado.value = EventoUiState(
            nombre = evento.nombre,
            tipo = evento.tipo,
            descripcion = evento.descripcion,
            fechaInicio = evento.fechaInicio,
            fechaTermino = evento.fechaTermino,
            horario = evento.horario,
            ubicacion = evento.ubicacion,
            comuna = evento.comuna,
            region = evento.region,
            organizacionId = evento.organizacionId,
            publicoObjetivo = evento.publicoObjetivo,
            publicoEsperado = evento.publicoEsperado,
            cantidadEstimadaAsistentes =
                evento.cantidadEstimadaAsistentes?.toString() ?: "",
            condicionesParticipacion = evento.condicionesParticipacion,
            requisitos = evento.requisitos,
            costosReferenciales = evento.costosReferenciales,
            espaciosDisponibles = evento.espaciosDisponibles,
            observaciones = evento.observaciones,
            esEdicion = true
        )
    }

    // Valida los campos obligatorios.
    fun validarFormulario(): Boolean {
        val actual = _estado.value

        val errores = EventoErrores(
            nombre = if (actual.nombre.isBlank()) {
                "El nombre del evento es obligatorio."
            } else null,

            tipo = if (actual.tipo.isBlank()) {
                "Selecciona un tipo de evento."
            } else null,

            fechaInicio = if (actual.fechaInicio.isBlank()) {
                "La fecha de inicio es obligatoria."
            } else null,

            fechaTermino = if (actual.fechaTermino.isBlank()) {
                "La fecha de término es obligatoria."
            } else null,

            ubicacion = if (actual.ubicacion.isBlank()) {
                "La ubicación es obligatoria."
            } else null,

            comuna = if (actual.comuna.isBlank()) {
                "La comuna es obligatoria."
            } else null,

            region = if (actual.region.isBlank()) {
                "La región es obligatoria."
            } else null,

            fechas = if (
                actual.fechaInicio.isNotBlank() &&
                actual.fechaTermino.isNotBlank() &&
                actual.fechaTermino < actual.fechaInicio
            ) {
                "La fecha de término no puede ser anterior a la fecha de inicio."
            } else null
        )

        _estado.update { it.copy(errores = errores) }

        return listOf(
            errores.nombre,
            errores.tipo,
            errores.fechaInicio,
            errores.fechaTermino,
            errores.ubicacion,
            errores.comuna,
            errores.region,
            errores.fechas
        ).all { it == null }
    }

    // Construye la entidad a partir del formulario y la guarda.
    fun guardarEvento() {
        if (!validarFormulario()) return

        val actual = _estado.value
        val idActual = idEventoEditando

        val evento = EventoEntity(
            id = idActual ?: 0,
            nombre = actual.nombre.trim(),
            tipo = actual.tipo.trim(),
            descripcion = actual.descripcion.trim(),
            fechaInicio = actual.fechaInicio.trim(),
            fechaTermino = actual.fechaTermino.trim(),
            horario = actual.horario.trim(),
            ubicacion = actual.ubicacion.trim(),
            comuna = actual.comuna.trim(),
            region = actual.region.trim(),
            organizacionId = actual.organizacionId,
            publicoObjetivo = actual.publicoObjetivo.trim(),
            publicoEsperado = actual.publicoEsperado.trim(),
            cantidadEstimadaAsistentes =
                actual.cantidadEstimadaAsistentes.toIntOrNull(),
            condicionesParticipacion = actual.condicionesParticipacion.trim(),
            requisitos = actual.requisitos.trim(),
            costosReferenciales = actual.costosReferenciales.trim(),
            espaciosDisponibles = actual.espaciosDisponibles.trim(),
            observaciones = actual.observaciones.trim()
        )

        viewModelScope.launch {
            try {
                if (idActual == null) {
                    repository.insertar(evento)
                    _mensaje.value = "Evento registrado correctamente."
                } else {
                    repository.actualizar(evento)
                    _mensaje.value = "Evento actualizado correctamente."
                }

                prepararNuevoEvento()

            } catch (e: Exception) {
                _mensaje.value =
                    "Error al guardar el evento: ${e.localizedMessage ?: "error desconocido"}"
            }
        }
    }

    // Conserva el método anterior para las llamadas existentes de EventoScreen.
    fun registrarEvento(evento: EventoEntity) {
        viewModelScope.launch {
            try {
                repository.insertar(evento)
                _mensaje.value = "Evento registrado correctamente."
            } catch (e: Exception) {
                _mensaje.value = "Error al registrar el evento."
            }
        }
    }

    // Conserva el método anterior para actualizar eventos.
    fun actualizarEvento(evento: EventoEntity) {
        viewModelScope.launch {
            try {
                repository.actualizar(evento)
                _mensaje.value = "Evento actualizado correctamente."
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar el evento."
            }
        }
    }

    // Elimina un evento.
    fun eliminarEvento(evento: EventoEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(evento)
                _mensaje.value = "Evento eliminado correctamente."
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar el evento."
            }
        }
    }

    // Consulta un evento por su identificador.
    suspend fun obtenerEvento(id: Int): EventoEntity? {
        return try {
            repository.obtenerPorId(id)
        } catch (e: Exception) {
            null
        }
    }

    // Consulta eventos asociados a una organización.
    fun obtenerEventosPorOrganizacion(organizacionId: Int) =
        repository.obtenerPorOrganizacion(organizacionId)

    // Limpia el mensaje mostrado en pantalla.
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}