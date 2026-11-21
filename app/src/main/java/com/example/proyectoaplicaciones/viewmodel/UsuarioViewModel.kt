package com.example.proyectoaplicaciones.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.UsuarioRequest
import com.example.proyectoaplicaciones.model.UsuarioErrores
import com.example.proyectoaplicaciones.model.UsuarioUiState
import com.example.proyectoaplicaciones.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Administra la lógica y el estado de la pantalla de registro.
class UsuarioViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    // Estado privado que puede modificar el ViewModel.
    private val _estado = MutableStateFlow(UsuarioUiState())

    // Estado público que observa la pantalla.
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    // Actualiza el nombre y limpia su error.
    fun onNombreChange(nuevoNombre: String) {
        _estado.value = _estado.value.copy(
            nombre = nuevoNombre,
            errores = _estado.value.errores.copy(
                nombre = null,
                registro = null
            )
        )
    }

    // Actualiza el correo y limpia su error.
    fun onCorreoChange(nuevoCorreo: String) {
        _estado.value = _estado.value.copy(
            correo = nuevoCorreo,
            errores = _estado.value.errores.copy(
                correo = null,
                registro = null
            )
        )
    }

    // Actualiza la contraseña y limpia su error.
    fun onContrasenaChange(nuevaContrasena: String) {
        _estado.value = _estado.value.copy(
            contrasena = nuevaContrasena,
            errores = _estado.value.errores.copy(
                contrasena = null,
                registro = null
            )
        )
    }

    // Actualiza el rol seleccionado.
    fun onRolChange(nuevoRol: String) {
        _estado.value = _estado.value.copy(
            rol = nuevoRol,
            errores = _estado.value.errores.copy(
                registro = null
            )
        )
    }

    // Valida los datos y registra al usuario en el backend.
    fun registrarUsuario() {

        val actual = _estado.value

        // Valida el nombre.
        val errorNombre = when {
            actual.nombre.isBlank() ->
                "Ingresa tu nombre"

            actual.nombre.trim().length < 3 ->
                "El nombre debe tener al menos 3 caracteres"

            actual.nombre.trim().length > 80 ->
                "El nombre no puede superar los 80 caracteres"

            else -> null
        }

        // Valida el correo.
        val errorCorreo = when {
            actual.correo.isBlank() ->
                "Ingresa tu correo"

            !Patterns.EMAIL_ADDRESS.matcher(
                actual.correo.trim()
            ).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        // Valida la contraseña.
        val errorContrasena = when {
            actual.contrasena.isBlank() ->
                "Ingresa una contraseña"

            actual.contrasena.length < 8 ->
                "La contraseña debe tener al menos 8 caracteres"

            else -> null
        }

        // Si existen errores, los muestra y detiene el registro.
        if (
            errorNombre != null ||
            errorCorreo != null ||
            errorContrasena != null
        ) {
            _estado.value = actual.copy(
                errores = UsuarioErrores(
                    nombre = errorNombre,
                    correo = errorCorreo,
                    contrasena = errorContrasena
                ),
                cargando = false
            )
            return
        }

        // Evita enviar varias solicitudes simultáneamente.
        if (actual.cargando) return

        // Activa el indicador de carga.
        _estado.value = actual.copy(
            errores = UsuarioErrores(),
            cargando = true,
            registroExitoso = false
        )

        viewModelScope.launch {

            try {

                // Construye el objeto que se enviará al backend.
                val usuario = UsuarioRequest(
                    nombre = actual.nombre.trim(),
                    correo = actual.correo.trim(),
                    password = actual.contrasena,
                    rol = actual.rol
                )

                // Envía la solicitud mediante el repositorio.
                val respuesta =
                    usuarioRepository.registrarUsuario(usuario)

                // Comprueba la respuesta del servidor.
                if (respuesta.isSuccessful && respuesta.body() != null) {

                    _estado.value = _estado.value.copy(
                        cargando = false,
                        registroExitoso = true,
                        errores = UsuarioErrores()
                    )

                } else {

                    _estado.value = _estado.value.copy(
                        cargando = false,
                        errores = UsuarioErrores(
                            registro = when (respuesta.code()) {
                                409 -> "El correo ya está registrado"
                                else -> "No se pudo registrar el usuario. Verifica los datos."
                            }
                        )
                    )
                }

            } catch (e: Exception) {

                // Maneja errores de conexión o comunicación.
                _estado.value = _estado.value.copy(
                    cargando = false,
                    errores = UsuarioErrores(
                        registro = "No fue posible conectar con el servidor"
                    )
                )
            }
        }
    }
}