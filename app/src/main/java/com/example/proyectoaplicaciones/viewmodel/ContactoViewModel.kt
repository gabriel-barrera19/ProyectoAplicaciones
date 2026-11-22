package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.local.ContactoEntity
import com.example.proyectoaplicaciones.model.ContactoErrores
import com.example.proyectoaplicaciones.model.ContactoUiState
import com.example.proyectoaplicaciones.repository.ContactoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del módulo de contactos.
 *
 * Gestiona el formulario, validaciones y operaciones
 * de registro, consulta, edición y eliminación.
 */
class ContactoViewModel(
    private val repository: ContactoRepository
) : ViewModel() {

    // Estado centralizado del formulario.
    private val _estado = MutableStateFlow(ContactoUiState())
    val estado: StateFlow<ContactoUiState> = _estado.asStateFlow()

    // Identificador del contacto que se está editando.
    private var idContactoEditando: Int? = null

    // Lista observable de todos los contactos.
    val contactos: StateFlow<List<ContactoEntity>> =
        repository.obtenerTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista observable para resultados de búsqueda y filtros.
    private val _contactosFiltrados =
        MutableStateFlow<List<ContactoEntity>>(emptyList())

    val contactosFiltrados: StateFlow<List<ContactoEntity>> =
        _contactosFiltrados.asStateFlow()

    // Mensajes de resultado de las operaciones.
    private val _resultadoOperacion =
        Channel<String>(Channel.BUFFERED)

    val resultadoOperacion = _resultadoOperacion.receiveAsFlow()

    // Actualización de los campos.
    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null)
            )
        }
    }

    fun onCargoChange(valor: String) {
        _estado.update {
            it.copy(
                cargo = valor,
                errores = it.errores.copy(cargo = null)
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update {
            it.copy(
                telefono = valor,
                errores = it.errores.copy(telefono = null)
            )
        }
    }

    fun onOrganizacionChange(id: Int) {
        _estado.update {
            it.copy(
                organizacionId = id,
                errores = it.errores.copy(organizacionId = null)
            )
        }
    }

    // Prepara el formulario para registrar un contacto.
    fun prepararNuevoContacto() {
        idContactoEditando = null
        _estado.value = ContactoUiState()
    }

    // Carga los datos de un contacto para editarlo.
    fun iniciarEdicion(contacto: ContactoEntity) {
        idContactoEditando = contacto.id

        _estado.value = ContactoUiState(
            nombre = contacto.nombre,
            cargo = contacto.cargo,
            correo = contacto.correo,
            telefono = contacto.telefono,
            organizacionId = contacto.organizacionId,
            esEdicion = true
        )
    }

    // Valida los campos del formulario.
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = ContactoErrores(
            nombre = if (actual.nombre.isBlank()) {
                "El nombre del contacto es obligatorio"
            } else null,

            cargo = if (actual.cargo.isBlank()) {
                "El cargo es obligatorio"
            } else null,

            correo = if (
                actual.correo.isBlank() ||
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(actual.correo.trim())
                    .matches()
            ) {
                "Ingresa un correo electrónico válido"
            } else null,

            telefono = if (actual.telefono.isBlank()) {
                "El teléfono es obligatorio"
            } else null,

            organizacionId = if (actual.organizacionId == null) {
                "Debes seleccionar una organización"
            } else null
        )

        _estado.update {
            it.copy(errores = errores)
        }

        return errores.nombre == null &&
                errores.cargo == null &&
                errores.correo == null &&
                errores.telefono == null &&
                errores.organizacionId == null
    }

    // Registra un contacto nuevo o actualiza uno existente.
    fun guardarContacto() {
        if (!validarFormulario()) return

        val actual = _estado.value

        val contacto = ContactoEntity(
            id = idContactoEditando ?: 0,
            organizacionId = actual.organizacionId!!,
            nombre = actual.nombre.trim(),
            cargo = actual.cargo.trim(),
            correo = actual.correo.trim(),
            telefono = actual.telefono.trim()
        )

        viewModelScope.launch {
            try {
                if (idContactoEditando == null) {
                    repository.insertar(contacto)
                    _resultadoOperacion.send(
                        "Contacto registrado exitosamente"
                    )
                } else {
                    repository.actualizar(contacto)
                    _resultadoOperacion.send(
                        "Contacto actualizado exitosamente"
                    )
                }

                prepararNuevoContacto()

            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al guardar el contacto"
                )
            }
        }
    }

    // Filtra los contactos por organización.
    fun obtenerContactosPorOrganizacion(organizacionId: Int) {
        viewModelScope.launch {
            try {
                repository.obtenerPorOrganizacion(organizacionId)
                    .collect { resultado ->
                        _contactosFiltrados.value = resultado
                    }
            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al consultar los contactos"
                )
            }
        }
    }

    // Busca contactos por nombre.
    fun buscarContactos(nombre: String) {
        viewModelScope.launch {
            try {
                repository.buscarPorNombre(nombre)
                    .collect { resultado ->
                        _contactosFiltrados.value = resultado
                    }
            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al buscar contactos"
                )
            }
        }
    }

    // Restablece la lista completa.
    fun limpiarFiltro() {
        _contactosFiltrados.value = contactos.value
    }

    // Elimina un contacto.
    fun eliminarContacto(contacto: ContactoEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(contacto)
                _resultadoOperacion.send(
                    "Contacto eliminado exitosamente"
                )
            } catch (e: Exception) {
                _resultadoOperacion.send(
                    "Error al eliminar el contacto"
                )
            }
        }
    }
}