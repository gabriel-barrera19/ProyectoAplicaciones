package com.example.proyectoaplicaciones.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoaplicaciones.data.model.LoginRequest
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import com.example.proyectoaplicaciones.data.remote.RetrofitInstance
import com.example.proyectoaplicaciones.model.LoginErrores
import com.example.proyectoaplicaciones.model.LoginUiState
import com.example.proyectoaplicaciones.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Administra la lógica y el estado de la pantalla de Login.
class LoginViewModel(
    private val usuarioRepository: UsuarioRepository =
        UsuarioRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Actualiza el correo y limpia sus errores.
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

    // Actualiza la contraseña y limpia sus errores.
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

    // Valida los datos y autentica al usuario en el backend.
    fun validarAcceso(
        alFinalizar: (UsuarioResponse?) -> Unit
    ) {
        val actual = _estado.value

        // Evita solicitudes simultáneas.
        if (actual.cargando) return

        // Validación del correo.
        val errorUsuario = when {
            actual.usuario.isBlank() ->
                "Ingresa tu correo"

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(actual.usuario.trim())
                .matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        // Validación de la contraseña.
        val errorContrasena =
            if (actual.contrasena.isBlank()) {
                "Ingresa tu contraseña"
            } else {
                null
            }

        // Si existen errores, no se consulta al backend.
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

        // Activa el indicador de carga.
        _estado.value = actual.copy(
            cargando = true,
            errores = LoginErrores(),
            usuarioAutenticado = null
        )

        viewModelScope.launch {
            try {
                // Prepara los datos de autenticación.
                val solicitud = LoginRequest(
                    correo = actual.usuario.trim(),
                    password = actual.contrasena
                )

                // Envía las credenciales al backend.
                val respuesta =
                    usuarioRepository.iniciarSesion(solicitud)

                if (respuesta.isSuccessful && respuesta.body() != null) {

                    // Obtiene el usuario autenticado.
                    val usuario = respuesta.body()!!

                    _estado.value = _estado.value.copy(
                        cargando = false,
                        errores = LoginErrores(),
                        usuarioAutenticado = usuario
                    )

                    // Devuelve los datos del usuario.
                    alFinalizar(usuario)

                } else {

                    // Maneja respuestas HTTP no exitosas.
                    val mensaje = when (respuesta.code()) {
                        400, 401, 403 ->
                            "Correo o contraseña incorrectos"

                        404 ->
                            "No se encontró el servicio de autenticación"

                        else ->
                            "Error del servidor (${respuesta.code()})"
                    }

                    _estado.value = _estado.value.copy(
                        cargando = false,
                        errores = LoginErrores(
                            credenciales = mensaje
                        )
                    )

                    alFinalizar(null)
                }

            } catch (e: IOException) {

                Log.e(
                    "LoginViewModel",
                    "Error de conexión con el backend",
                    e
                )

                _estado.value = _estado.value.copy(
                    cargando = false,
                    errores = LoginErrores(
                        credenciales = "No se pudo conectar con el servidor. Comprueba que esté iniciado."
                    )
                )

                alFinalizar(null)

            } catch (e: Exception) {

                Log.e(
                    "LoginViewModel",
                    "Error inesperado durante el login",
                    e
                )

                _estado.value = _estado.value.copy(
                    cargando = false,
                    errores = LoginErrores(
                        credenciales = "Ocurrió un error al iniciar sesión."
                    )
                )

                alFinalizar(null)
            }
        }
    }
}