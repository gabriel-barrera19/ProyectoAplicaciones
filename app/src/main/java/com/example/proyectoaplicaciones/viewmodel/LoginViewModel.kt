package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectoaplicaciones.model.LoginErrores
import com.example.proyectoaplicaciones.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Administra la lógica y el estado de la pantalla de Login.
class LoginViewModel : ViewModel() {

    // Estado privado que puede modificar el ViewModel.
    private val _estado = MutableStateFlow(LoginUiState())

    // Estado público que observa la pantalla.
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Actualiza el usuario y limpia sus errores.
    fun onUsuarioChange(nuevoUsuario: String) {
        _estado.value = _estado.value.copy(
            usuario = nuevoUsuario,
            errores = _estado.value.errores.copy(
                usuario = null,
                credenciales = null
            )
        )
    }

    // Actualiza la contraseña y limpia sus errores.
    fun onContrasenaChange(nuevaContrasena: String) {
        _estado.value = _estado.value.copy(
            contrasena = nuevaContrasena,
            errores = _estado.value.errores.copy(
                contrasena = null,
                credenciales = null
            )
        )
    }

    // Comprueba los campos y las credenciales de demostración.
    fun validarAcceso(): Boolean {

        val actual = _estado.value

        // Comprueba si los campos están vacíos.
        val errorUsuario = if (actual.usuario.isBlank()) {
            "Ingresa tu usuario"
        } else {
            null
        }

        val errorContrasena = if (actual.contrasena.isBlank()) {
            "Ingresa tu contraseña"
        } else {
            null
        }

        // Si falta información, devuelve los errores de los campos.
        if (errorUsuario != null || errorContrasena != null) {
            _estado.value = actual.copy(
                errores = LoginErrores(
                    usuario = errorUsuario,
                    contrasena = errorContrasena
                )
            )
            return false
        }

        // Credenciales ficticias para probar el funcionamiento del Login.
        val accesoValido =
            actual.usuario.trim().equals(
                "comercial@demo.cl",
                ignoreCase = true
            ) && actual.contrasena == "Demo1234"

        // Muestra un error general si los datos no coinciden.
        _estado.value = actual.copy(
            errores = LoginErrores(
                credenciales = if (accesoValido) {
                    null
                } else {
                    "Usuario o contraseña incorrectos"
                }
            )
        )

        return accesoValido
    }
}