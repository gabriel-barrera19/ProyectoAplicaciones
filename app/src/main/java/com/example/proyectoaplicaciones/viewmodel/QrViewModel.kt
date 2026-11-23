package com.example.proyectoaplicaciones.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectoaplicaciones.model.OportunidadUiState
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QrViewModel : ViewModel() {

    private val _resultadoQr = MutableStateFlow("")
    val resultadoQr: StateFlow<String> = _resultadoQr.asStateFlow()

    private val _oportunidadQr = MutableStateFlow<OportunidadUiState?>(null)
    val oportunidadQr: StateFlow<OportunidadUiState?> =
        _oportunidadQr.asStateFlow()

    private val _mensajeError = MutableStateFlow("")
    val mensajeError: StateFlow<String> = _mensajeError.asStateFlow()

    fun alDetectarCodigo(codigo: String) {
        if (codigo.isBlank()) return

        _resultadoQr.value = codigo
        _oportunidadQr.value = null
        _mensajeError.value = ""

        try {
            val json = JsonParser.parseString(codigo).asJsonObject

            val tipo = json.get("tipo")?.asString
            val version = json.get("version")?.asInt

            require(tipo == "OPORTUNIDAD") {
                "El código QR no corresponde a una oportunidad."
            }

            require(version == 1) {
                "La versión del código QR no es compatible."
            }

            val datos = json.getAsJsonObject("datos")
                ?: throw IllegalArgumentException(
                    "El código QR no contiene los datos de la oportunidad."
                )

            fun campoObligatorio(nombre: String): String {
                val valor = datos.get(nombre)

                require(valor != null && !valor.isJsonNull) {
                    "Falta el campo obligatorio: $nombre"
                }

                val texto = valor.asString.trim()

                require(texto.isNotBlank()) {
                    "El campo $nombre no puede estar vacío."
                }

                return texto
            }

            val eventoId = datos.get("eventoId")
                ?.takeUnless { it.isJsonNull }
                ?.asInt

            val oportunidad = OportunidadUiState(
                nombre = campoObligatorio("nombre"),
                descripcion = campoObligatorio("descripcion"),
                tipo = campoObligatorio("tipoOportunidad"),
                fuente = campoObligatorio("fuente"),
                prioridad = campoObligatorio("prioridad"),
                potencialComercial = campoObligatorio("potencialComercial"),
                estado = campoObligatorio("estado"),
                fecha = campoObligatorio("fecha"),
                observaciones = datos.get("observaciones")
                    ?.takeUnless { it.isJsonNull }
                    ?.asString ?: "",
                eventoId = eventoId,
                errores = com.example.proyectoaplicaciones.model.OportunidadErrores(),
                esEdicion = false
            )

            require(eventoId == null || eventoId > 0) {
                "El identificador del evento no es válido."
            }

            _oportunidadQr.value = oportunidad
            _mensajeError.value = "Oportunidad cargada correctamente."

        } catch (e: Exception) {
            _oportunidadQr.value = null
            _mensajeError.value =
                e.message ?: "No fue posible procesar el código QR."
        }
    }

    fun limpiarResultado() {
        _resultadoQr.value = ""
        _oportunidadQr.value = null
        _mensajeError.value = ""
    }
}