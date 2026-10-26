package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrViewModel : ViewModel() {

    // Estado privado que administra el resultado del escaneo.
    private val _resultadoQr = MutableStateFlow("")

    // Estado público que observa la interfaz.
    val resultadoQr: StateFlow<String> = _resultadoQr.asStateFlow()

    // Guarda el contenido leído desde el código QR.
    fun alDetectarCodigo(codigo: String) {
        if (codigo.isNotBlank()) {
            _resultadoQr.value = codigo
        }
    }

    // Limpia el resultado para permitir un nuevo escaneo.
    fun limpiarResultado() {
        _resultadoQr.value = ""
    }
}