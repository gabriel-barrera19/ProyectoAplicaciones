package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.OportunidadEntity
import com.example.proyectoaplicaciones.data.repository.OportunidadRepository
import com.example.proyectoaplicaciones.model.OportunidadErrores
import com.example.proyectoaplicaciones.model.OportunidadUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del módulo de oportunidades.
 *
 * Gestiona el formulario, sus validaciones y los datos
 * que se guardan en la base de datos mediante el repositorio.
 */
class OportunidadViewModel(
    private val repository: OportunidadRepository
) : ViewModel() {

    // Estado privado que el ViewModel puede modificar.
    private val _estado = MutableStateFlow(OportunidadUiState())

    // Estado público de solo lectura para la pantalla.
    val estado: StateFlow<OportunidadUiState> = _estado.asStateFlow()

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

        // Publica los errores para mostrarlos en pantalla.
        _estado.update { it.copy(errores = errores) }

        // El formulario es válido si todos los errores son nulos.
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

    // Guarda la oportunidad si todos los campos son válidos.
    fun guardarOportunidad() {
        if (!validarFormulario()) return

        // Captura los datos actuales del formulario.
        val actual = _estado.value

        // Convierte los datos del formulario en una entidad Room.
        val oportunidad = OportunidadEntity(
            nombre = actual.nombre.trim(),
            descripcion = actual.descripcion.trim(),
            tipo = actual.tipo,
            fuente = actual.fuente.trim(),
            prioridad = actual.prioridad,
            potencialComercial = actual.potencialComercial,
            estado = actual.estado,
            observaciones = actual.observaciones.trim()
        )

        // Ejecuta la inserción sin bloquear la interfaz.
        viewModelScope.launch {
            repository.insertar(oportunidad)

            // Limpia el formulario después de guardar.
            _estado.value = OportunidadUiState()
        }
    }

    // Elimina una oportunidad mediante el repositorio.
    fun eliminarOportunidad(oportunidad: OportunidadEntity) {
        viewModelScope.launch {
            repository.eliminar(oportunidad)
        }
    }
}