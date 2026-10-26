package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectoaplicaciones.model.LoginErrores
import com.example.proyectoaplicaciones.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // Valida las credenciales y muestra el estado de carga.
    fun validarAcceso(alFinalizar: (Boolean) -> Unit) {

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

        // Si faltan datos, muestra los errores sin iniciar la carga.
        if (errorUsuario != null || errorContrasena != null) {
            _estado.value = actual.copy(
                errores = LoginErrores(
                    usuario = errorUsuario,
                    contrasena = errorContrasena
                ),
                cargando = false
            )
            return
        }

        // Evita procesar varios intentos simultáneamente.
        if (actual.cargando) return

        // Activa el indicador de carga y limpia los errores anteriores.
        _estado.value = actual.copy(
            errores = LoginErrores(),
            cargando = true
        )

        viewModelScope.launch {
            // Permite que el usuario vea el indicador de carga.
            delay(1500)

            val accesoValido =
                actual.usuario.trim().equals(
                    "comercial@demo.cl",
                    ignoreCase = true
                ) && actual.contrasena == "Demo1234"

            // Actualiza el estado y muestra el resultado.
            _estado.value = _estado.value.copy(
                cargando = false,
                errores = LoginErrores(
                    credenciales = if (accesoValido) {
                        null
                    } else {
                        "Usuario o contraseña incorrectos"
                    }
                )
            )

            // Informa a la pantalla si el ingreso fue correcto.
            alFinalizar(accesoValido)
        }
    }
}