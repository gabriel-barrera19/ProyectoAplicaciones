package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.OrganizacionEntity
import com.example.proyectoaplicaciones.model.OrganizacionErrores
import com.example.proyectoaplicaciones.model.OrganizacionUiState
import com.example.proyectoaplicaciones.repository.OrganizacionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del módulo de organizaciones.
 *
 * Gestiona el formulario, las validaciones y las operaciones
 * de registro, edición, búsqueda y eliminación.
 */
class OrganizacionViewModel(
    private val repository: OrganizacionRepository
) : ViewModel() {

    // Estado del formulario.
    private val _estado = MutableStateFlow(OrganizacionUiState())

    // Estado público que observa la interfaz.
    val estado: StateFlow<OrganizacionUiState> = _estado.asStateFlow()

    // Comunica a la pantalla el resultado de las operaciones.
    private val _resultadoOperacion = Channel<String>(Channel.BUFFERED)

    val resultadoOperacion = _resultadoOperacion.receiveAsFlow()

    // Guarda el ID de la organización que se está editando.
    private var idOrganizacionEditando: Int? = null

    // Lista observable de organizaciones almacenadas en Room.
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
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

    // Actualiza el rubro y limpia su error.
    fun onRubroChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                rubro = valor,
                errores = actual.errores.copy(rubro = null)
            )
        }
    }

    // Actualiza el tipo y limpia su error.
    fun onTipoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                tipo = valor,
                errores = actual.errores.copy(tipo = null)
            )
        }
    }

    // Actualiza la dirección y limpia su error.
    fun onDireccionChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                direccion = valor,
                errores = actual.errores.copy(direccion = null)
            )
        }
    }

    // Actualiza la comuna y limpia su error.
    fun onComunaChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                comuna = valor,
                errores = actual.errores.copy(comuna = null)
            )
        }
    }

    // Actualiza la región y limpia su error.
    fun onRegionChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                region = valor,
                errores = actual.errores.copy(region = null)
            )
        }
    }

    // Actualiza el teléfono y limpia su error.
    fun onTelefonoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                telefono = valor,
                errores = actual.errores.copy(telefono = null)
            )
        }
    }

    // Actualiza el correo y limpia su error.
    fun onCorreoChange(valor: String) {
        _estado.update { actual ->
            actual.copy(
                correo = valor,
                errores = actual.errores.copy(correo = null)
            )
        }
    }

    // Actualiza la descripción opcional.
    fun onDescripcionChange(valor: String) {
        _estado.update { actual ->
            actual.copy(descripcion = valor)
        }
    }

    // Prepara el formulario para registrar una organización nueva.
    fun prepararNuevaOrganizacion() {
        idOrganizacionEditando = null
        _estado.value = OrganizacionUiState()
    }

    // Carga una organización para editarla.
    fun iniciarEdicion(organizacion: OrganizacionEntity) {
        idOrganizacionEditando = organizacion.id

        _estado.value = OrganizacionUiState(
            nombre = organizacion.nombre,
            rubro = organizacion.rubro,
            tipo = organizacion.tipo,
            direccion = organizacion.direccion,
            comuna = organizacion.comuna,
            region = organizacion.region,
            telefono = organizacion.telefono,
            correo = organizacion.correo,
            descripcion = organizacion.descripcion,
            esEdicion = true
        )
    }

    // Valida los campos obligatorios del formulario.
    fun validarFormulario(): Boolean {
        val actual = _estado.value

        val errores = OrganizacionErrores(
            nombre = if (actual.nombre.isBlank()) {
                "El nombre es obligatorio"
            } else null,

            rubro = if (actual.rubro.isBlank()) {
                "El rubro es obligatorio"
            } else null,

            tipo = if (actual.tipo.isBlank()) {
                "Selecciona un tipo de organización"
            } else null,

            direccion = if (actual.direccion.isBlank()) {
                "La dirección es obligatoria"
            } else null,

            comuna = if (actual.comuna.isBlank()) {
                "La comuna es obligatoria"
            } else null,

            region = if (actual.region.isBlank()) {
                "La región es obligatoria"
            } else null,

            telefono = if (actual.telefono.isBlank()) {
                "El teléfono es obligatorio"
            } else null,

            correo = if (
                actual.correo.isBlank() ||
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(actual.correo.trim())
                    .matches()
            ) {
                "Ingresa un correo electrónico válido"
            } else null
        )

        // Publica los errores para mostrarlos en la pantalla.
        _estado.update { it.copy(errores = errores) }

        // Comprueba que todos los campos obligatorios sean válidos.
        return listOf(
            errores.nombre,
            errores.rubro,
            errores.tipo,
            errores.direccion,
            errores.comuna,
            errores.region,
            errores.telefono,
            errores.correo
        ).all { it == null }
    }

    // Registra una organización nueva o actualiza una existente.
    fun guardarOrganizacion() {

        // No guarda si existen errores de validación.
        if (!validarFormulario()) return

        val actual = _estado.value
        val idActual = idOrganizacionEditando

        // Construye la entidad con los datos del formulario.
        val organizacion = OrganizacionEntity(
            id = idActual ?: 0,
            nombre = actual.nombre.trim(),
            rubro = actual.rubro.trim(),
            tipo = actual.tipo.trim(),
            direccion = actual.direccion.trim(),
            comuna = actual.comuna.trim(),
            region = actual.region.trim(),
            telefono = actual.telefono.trim(),
            correo = actual.correo.trim(),
            descripcion = actual.descripcion.trim()
        )

        // Ejecuta la operación de base de datos sin bloquear la UI.
        viewModelScope.launch {
            try {
                if (idActual == null) {

                    repository.insertar(organizacion)

                    _resultadoOperacion.send(
                        "Organización registrada exitosamente"
                    )

                } else {

                    repository.actualizar(organizacion)

                    _resultadoOperacion.send(
                        "Organización actualizada exitosamente"
                    )
                }

                // Restablece el formulario después de guardar.
                prepararNuevaOrganizacion()

            } catch (e: Exception) {

                _resultadoOperacion.send(
                    "Error al guardar la organización"
                )
            }
        }
    }

    // Busca organizaciones por nombre.
    fun buscarOrganizaciones(nombre: String) {
        viewModelScope.launch {
            try {
                repository.buscarPorNombre(nombre).collect { resultado ->
                    // La búsqueda se observará posteriormente desde la UI.
                }
            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al buscar organizaciones"
                )
            }
        }
    }

    // Elimina una organización mediante el repositorio.
    fun eliminarOrganizacion(organizacion: OrganizacionEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(organizacion)

                _resultadoOperacion.send(
                    "Organización eliminada exitosamente"
                )

            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al eliminar la organización"
                )
            }
        }
    }
}