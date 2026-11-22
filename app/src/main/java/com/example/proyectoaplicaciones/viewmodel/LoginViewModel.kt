package com.example.proyectoaplicaciones.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.proyectoaplicaciones.model.LoginErrores
import com.example.proyectoaplicaciones.model.LoginUiState
import com.example.proyectoaplicaciones.model.UsuarioLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Administra el inicio de sesión mediante usuarios locales.
class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Usuarios disponibles localmente.
    private data class CredencialLocal(
        val usuario: UsuarioLocal,
        val contrasena: String
    )

    private val usuariosLocales = listOf(
        CredencialLocal(
            usuario = UsuarioLocal(
                nombre = "Ejecutivo Comercial",
                correo = "comercial@demo.cl",
                rol = "EJECUTIVO_COMERCIAL"
            ),
            contrasena = "Demo1234"
        ),
        CredencialLocal(
            usuario = UsuarioLocal(
                nombre = "Supervisor Comercial",
                correo = "supervisor@demo.cl",
                rol = "SUPERVISOR_COMERCIAL"
            ),
            contrasena = "Super1234"
        )
    )

    // Actualiza el correo ingresado.
    fun onUsuarioChange(nuevoUsuario: String) {
        _estado.value = _estado.value.copy(
            usuario = nuevoUsuario,
            errores = _estado.value.errores.copy(
                usuario = null,
                credenciales = null
            ),
            usuarioAutenticado = null
        )
    }

    // Actualiza la contraseña ingresada.
    fun onContrasenaChange(nuevaContrasena: String) {
        _estado.value = _estado.value.copy(
            contrasena = nuevaContrasena,
            errores = _estado.value.errores.copy(
                contrasena = null,
                credenciales = null
            ),
            usuarioAutenticado = null
        )
    }

    // Valida las credenciales sin consultar ningún servidor.
    fun validarAcceso(
        alFinalizar: (UsuarioLocal?) -> Unit
    ) {
        val actual = _estado.value

        if (actual.cargando) return

        val correo = actual.usuario.trim()

        val errorUsuario = when {
            correo.isBlank() -> "Ingresa tu correo"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Ingresa un correo válido"
            else -> null
        }

        val errorContrasena = if (actual.contrasena.isBlank()) {
            "Ingresa tu contraseña"
        } else {
            null
        }

        if (errorUsuario != null || errorContrasena != null) {
            _estado.value = actual.copy(
                errores = LoginErrores(
                    usuario = errorUsuario,
                    contrasena = errorContrasena
                ),
                cargando = false
            )
            alFinalizar(null)
            return
        }

        val credencialEncontrada = usuariosLocales.firstOrNull {
            it.usuario.correo.equals(correo, ignoreCase = true) &&
                    it.contrasena == actual.contrasena
        }

        if (credencialEncontrada != null) {
            val usuario = credencialEncontrada.usuario

            _estado.value = actual.copy(
                cargando = false,
                errores = LoginErrores(),
                usuarioAutenticado = usuario
            )

            alFinalizar(usuario)
        } else {
            _estado.value = actual.copy(
                cargando = false,
                errores = LoginErrores(
                    credenciales = "Correo o contraseña incorrectos"
                ),
                usuarioAutenticado = null
            )

            alFinalizar(null)
        }
    }
}