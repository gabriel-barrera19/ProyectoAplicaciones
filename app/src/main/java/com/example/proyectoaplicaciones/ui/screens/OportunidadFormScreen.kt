package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// Pantalla para registrar una oportunidad comercial.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    viewModel: OportunidadViewModel,
    alVolver: () -> Unit
) {
    // Observa el estado que administra el ViewModel.
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar oportunidad")
                },
                navigationIcon = {
                    IconButton(
                        onClick = alVolver
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Respeta el espacio de la barra superior.
                .padding(paddingInterno)
                // Mantiene los márgenes del formulario.
                .padding(horizontal = 20.dp, vertical = 16.dp)
                // Permite desplazarse verticalmente por los campos.
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Campo: nombre.
            CampoTexto(
                etiqueta = "Nombre de la oportunidad",
                valor = estado.nombre,
                error = estado.errores.nombre,
                alCambiar = viewModel::onNombreChange
            )

            // Campo: descripción.
            CampoTexto(
                etiqueta = "Descripción",
                valor = estado.descripcion,
                error = estado.errores.descripcion,
                alCambiar = viewModel::onDescripcionChange,
                unaLinea = false
            )

            // Selección: tipo.
            CampoSeleccion(
                etiqueta = "Tipo de oportunidad",
                valor = estado.tipo,
                opciones = listOf(
                    "Feria",
                    "Congreso",
                    "Activación corporativa",
                    "Otro"
                ),
                error = estado.errores.tipo,
                alSeleccionar = viewModel::onTipoChange
            )

            // Campo: fuente de información.
            CampoTexto(
                etiqueta = "Fuente de información",
                valor = estado.fuente,
                error = estado.errores.fuente,
                alCambiar = viewModel::onFuenteChange
            )

            // Selección: prioridad.
            CampoSeleccion(
                etiqueta = "Prioridad",
                valor = estado.prioridad,
                opciones = listOf(
                    "Alta",
                    "Media",
                    "Baja"
                ),
                error = estado.errores.prioridad,
                alSeleccionar = viewModel::onPrioridadChange
            )

            // Selección: potencial comercial.
            CampoSeleccion(
                etiqueta = "Potencial comercial",
                valor = estado.potencialComercial,
                opciones = listOf(
                    "Alto",
                    "Medio",
                    "Bajo"
                ),
                error = estado.errores.potencialComercial,
                alSeleccionar = viewModel::onPotencialComercialChange
            )

            // Selección: estado de gestión.
            CampoSeleccion(
                etiqueta = "Estado de gestión",
                valor = estado.estado,
                opciones = listOf(
                    "Evento identificado",
                    "En evaluación",
                    "Propuesta enviada",
                    "Cerrada"
                ),
                error = estado.errores.estado,
                alSeleccionar = viewModel::onEstadoChange
            )

            // Campo opcional: observaciones.
            CampoTexto(
                etiqueta = "Observaciones (opcional)",
                valor = estado.observaciones,
                error = null,
                alCambiar = viewModel::onObservacionesChange,
                unaLinea = false
            )

            // Guarda la oportunidad si el formulario es válido.
            Button(
                onClick = {
                    viewModel.guardarOportunidad()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar oportunidad")
            }
        }
    }
}

// Componente reutilizable para campos de texto.
@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    error: String?,
    alCambiar: (String) -> Unit,
    unaLinea: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = {
            Text(etiqueta)
        },
        modifier = Modifier.fillMaxWidth(),
        isError = error != null,
        singleLine = unaLinea,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

// Componente reutilizable para listas de opciones.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoSeleccion(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    error: String?,
    alSeleccionar: (String) -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = {
            expandido = !expandido
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(etiqueta)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            },
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}