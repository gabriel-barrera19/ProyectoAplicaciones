package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.OportunidadEntity
import com.example.proyectoaplicaciones.repository.OportunidadRepository
import com.example.proyectoaplicaciones.model.OportunidadErrores
import com.example.proyectoaplicaciones.model.OportunidadUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * ViewModel del módulo de oportunidades.
 *
 * Gestiona el formulario, las validaciones y las operaciones
 * de registro, edición y eliminación de oportunidades.
 */
class OportunidadViewModel(
    private val repository: OportunidadRepository
) : ViewModel() {

    // Estado del formulario que el ViewModel puede modificar.
    private val _estado = MutableStateFlow(OportunidadUiState())

    // Estado público que observa la interfaz.
    val estado: StateFlow<OportunidadUiState> = _estado.asStateFlow()

    // Comunica a la pantalla el resultado de guardar una oportunidad.
    private val _resultadoGuardado = Channel<String>(Channel.BUFFERED)

    val resultadoGuardado = _resultadoGuardado.receiveAsFlow()

    // Guarda el ID de la oportunidad que se está editando.
    private var idOportunidadEditando: Int? = null

    // Lista observable de oportunidades almacenadas en Room.
    val oportunidades: StateFlow<List<OportunidadEntity>> =
        repository.obtenerTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Actualiza el nombre y limpia su error.
    fun onNombreChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                nombre = valor,
                errores = actual.errores.copy(nombre = null)
            )
        }
    }

    // Actualiza la descripción y limpia su error.
    fun onDescripcionChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                descripcion = valor,
                errores = actual.errores.copy(descripcion = null)
            )
        }
    }

    // Actualiza el tipo de oportunidad.
    fun onTipoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                tipo = valor,
                errores = actual.errores.copy(tipo = null)
            )
        }
    }

    // Actualiza la fuente de información.
    fun onFuenteChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                fuente = valor,
                errores = actual.errores.copy(fuente = null)
            )
        }
    }

    // Actualiza la prioridad.
    fun onPrioridadChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                prioridad = valor,
                errores = actual.errores.copy(prioridad = null)
            )
        }
    }

    // Actualiza el potencial comercial.
    fun onPotencialComercialChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                potencialComercial = valor,
                errores = actual.errores.copy(
                    potencialComercial = null
                )
            )
        }
    }

    // Actualiza el estado de gestión.
    fun onEstadoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                estado = valor,
                errores = actual.errores.copy(estado = null)
            )
        }
    }

    // Actualiza las observaciones opcionales.
    fun onObservacionesChange(valor: String) {
        _estado.update { actual ->
            actual.copy(observaciones = valor)
        }
    }

    // Actualiza la fecha seleccionada.
    fun onFechaChange(nuevaFecha: String) {
        _estado.update { actual ->
            actual.copy(fecha = nuevaFecha)
        }
    }

    // Actualiza el evento asociado a la oportunidad.
    // Si recibe null, la oportunidad queda sin evento asociado.
    fun onEventoIdChange(nuevoEventoId: Int?) {
        _estado.update { actual ->
            actual.copy(eventoId = nuevoEventoId)
        }
    }

    // Prepara el formulario para registrar una oportunidad nueva.
    fun prepararNuevaOportunidad() {
        idOportunidadEditando = null
        _estado.value = OportunidadUiState()
    }

    // Carga los datos de una oportunidad seleccionada para editarla.
    fun iniciarEdicion(oportunidad: OportunidadEntity) {
        idOportunidadEditando = oportunidad.id

        _estado.value = OportunidadUiState(
            nombre = oportunidad.nombre,
            descripcion = oportunidad.descripcion,
            tipo = oportunidad.tipo,
            fuente = oportunidad.fuente,
            prioridad = oportunidad.prioridad,
            potencialComercial = oportunidad.potencialComercial,
            estado = oportunidad.estado,
            observaciones = oportunidad.observaciones,
            fecha = oportunidad.fecha,
            eventoId = oportunidad.eventoId,
            esEdicion = true
        )
    }

    // Comprueba los campos obligatorios del formulario.
    fun validarFormulario(): Boolean {
        val actual = _estado.value

        val errores = OportunidadErrores(
            nombre = if (actual.nombre.isBlank()) {
                "El nombre es obligatorio"
            } else null,

            descripcion = if (actual.descripcion.isBlank()) {
                "La descripción es obligatoria"
            } else null,

            tipo = if (actual.tipo.isBlank()) {
                "Selecciona un tipo de oportunidad"
            } else null,

            fuente = if (actual.fuente.isBlank()) {
                "La fuente de información es obligatoria"
            } else null,

            prioridad = if (actual.prioridad.isBlank()) {
                "Selecciona una prioridad"
            } else null,

            potencialComercial =
                if (actual.potencialComercial.isBlank()) {
                    "Selecciona el potencial comercial"
                } else null,

            estado = if (actual.estado.isBlank()) {
                "Selecciona un estado de gestión"
            } else null
        )

        _estado.update { it.copy(errores = errores) }

        return listOf(
            errores.nombre,
            errores.descripcion,
            errores.tipo,
            errores.fuente,
            errores.prioridad,
            errores.potencialComercial,
            errores.estado
        ).all { it == null }
    }

    // Registra una oportunidad nueva o actualiza una existente.
    fun guardarOportunidad() {
        if (!validarFormulario()) return

        val actual = _estado.value
        val idActual = idOportunidadEditando

        // Construye la entidad con los datos del formulario.
        val oportunidad = OportunidadEntity(
            id = idActual ?: 0,
            nombre = actual.nombre.trim(),
            descripcion = actual.descripcion.trim(),
            tipo = actual.tipo,
            fuente = actual.fuente.trim(),
            prioridad = actual.prioridad,
            potencialComercial = actual.potencialComercial,
            estado = actual.estado,
            fecha = actual.fecha,
            observaciones = actual.observaciones.trim(),

            // Guarda la relación con el evento seleccionado.
            eventoId = actual.eventoId
        )

        viewModelScope.launch {
            try {
                if (idActual == null) {
                    repository.insertar(oportunidad)

                    _resultadoGuardado.send(
                        "Oportunidad creada exitosamente"
                    )
                } else {
                    repository.actualizar(oportunidad)

                    _resultadoGuardado.send(
                        "Oportunidad actualizada exitosamente"
                    )
                }

                prepararNuevaOportunidad()

            } catch (e: Exception) {
                _resultadoGuardado.send(
                    "Error al guardar la oportunidad: ${e.message}"
                )
            }
        }
    }

    // Elimina una oportunidad mediante el repositorio.
    fun eliminarOportunidad(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            repository.eliminar(oportunidad)
        }
    }
}