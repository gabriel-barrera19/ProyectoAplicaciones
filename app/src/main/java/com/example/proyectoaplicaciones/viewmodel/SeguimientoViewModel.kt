package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.SeguimientoEntity
import com.example.proyectoaplicaciones.repository.SeguimientoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SeguimientoViewModel(
    private val repository: SeguimientoRepository
) : ViewModel() {

    // Identificador de la oportunidad cuyo historial se consulta.
    private val _oportunidadId = MutableStateFlow<Int?>(null)

    // Lista observable de seguimientos.
    val seguimientos: StateFlow<List<SeguimientoEntity>> =
        _oportunidadId
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(emptyList())
                } else {
                    repository.obtenerPorOportunidad(id)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Mensajes de resultado de las operaciones.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Selecciona la oportunidad cuyo historial se mostrará.
    fun cargarSeguimientos(oportunidadId: Int) {
        _oportunidadId.value = oportunidadId
    }

    // Registra un seguimiento nuevo.
    fun registrarSeguimiento(
        oportunidadId: Int,
        fecha: String,
        tipoGestion: String,
        resultado: String,
        observaciones: String
    ) {
        if (fecha.isBlank() ||
            tipoGestion.isBlank() ||
            resultado.isBlank()
        ) {
            _mensaje.value = "Completa los campos obligatorios."
            return
        }

        viewModelScope.launch {
            try {
                val seguimiento = SeguimientoEntity(
                    oportunidadId = oportunidadId,
                    fecha = fecha.trim(),
                    tipoGestion = tipoGestion.trim(),
                    resultado = resultado.trim(),
                    observaciones = observaciones.trim()
                )

                repository.insertar(seguimiento)

                _mensaje.value = "Seguimiento registrado correctamente."

            } catch (e: Exception) {
                _mensaje.value = "Error al registrar el seguimiento."
            }
        }
    }

    // Elimina un seguimiento existente.
    fun eliminarSeguimiento(seguimiento: SeguimientoEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(seguimiento)
                _mensaje.value = "Seguimiento eliminado correctamente."
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar el seguimiento."
            }
        }
    }

    // Limpia el mensaje mostrado en pantalla.
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}