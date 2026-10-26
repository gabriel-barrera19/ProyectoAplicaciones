package com.example.proyectoaplicaciones.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import java.util.Calendar
import java.util.Locale

// Pantalla para registrar o editar una oportunidad comercial.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    viewModel: OportunidadViewModel,
    alVolver: () -> Unit,
    alIrMenuPrincipal: () -> Unit,
    alVerOportunidades: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    // Observa el estado que administra el ViewModel.
    val estado by viewModel.estado.collectAsState()

    // Controla la aparición animada del formulario.
    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    // Activa la animación al mostrar la pantalla.
    LaunchedEffect(Unit) {
        mostrarFormulario = true
    }

    // Guarda el mensaje de confirmación del guardado.
    var mensajeConfirmacion by remember {
        mutableStateOf<String?>(null)
    }

    // Escucha el resultado que envía el ViewModel después de guardar.
    LaunchedEffect(viewModel) {
        viewModel.resultadoGuardado.collect { mensaje ->
            mensajeConfirmacion = mensaje
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (estado.esEdicion) {
                            "Editar oportunidad"
                        } else {
                            "Registrar oportunidad"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = alVolver
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la lista"
                        )
                    }
                },
                actions = {
                    MenuAplicacion(
                        alIrMenuPrincipal = alIrMenuPrincipal,
                        alVerOportunidades = alVerOportunidades,
                        alCerrarSesion = alCerrarSesion
                    )
                }
            )
        }
    ) { paddingInterno ->

        // Animación de entrada del formulario.
        AnimatedVisibility(
            visible = mostrarFormulario,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 600)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 600),
                initialOffsetY = { altura -> altura / 10 }
            )
        ) {
            // Contenedor principal del formulario.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
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

                // Campo para seleccionar la fecha de la oportunidad.
                CampoFecha(
                    fecha = estado.fecha,
                    alSeleccionar = viewModel::onFechaChange
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
                    Text(
                        if (estado.esEdicion) {
                            "Guardar cambios"
                        } else {
                            "Registrar oportunidad"
                        }
                    )
                }
            }
        }

        // El diálogo permanece fuera de la animación del formulario.
        if (mensajeConfirmacion != null) {
            AlertDialog(
                onDismissRequest = {
                    mensajeConfirmacion = null
                },
                title = {
                    Text(
                        if (mensajeConfirmacion!!.startsWith("Error")) {
                            "No se pudo guardar"
                        } else {
                            "Operación exitosa"
                        }
                    )
                },
                text = {
                    Text(mensajeConfirmacion!!)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mensajeConfirmacion = null

                            // Regresa a Gestión de Oportunidades.
                            alVolver()
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
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

/**
 * Componente reutilizable para seleccionar una fecha.
 * La fecha se devuelve en formato AAAA-MM-DD.
 */
@Composable
private fun CampoFecha(
    fecha: String,
    alSeleccionar: (String) -> Unit
) {
    // Obtiene el contexto de Android para abrir el calendario.
    val contexto = LocalContext.current

    // Muestra la fecha seleccionada en un campo de solo lectura.
    OutlinedTextField(
        value = fecha,
        onValueChange = {},
        label = {
            Text("Fecha de la oportunidad")
        },
        placeholder = {
            Text("Seleccionar fecha")
        },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            TextButton(
                onClick = {
                    // Obtiene la fecha actual para iniciar el calendario.
                    val calendario = Calendar.getInstance()

                    // Si ya existe una fecha, intenta utilizarla.
                    if (fecha.isNotBlank()) {
                        val partes = fecha.split("-")

                        if (partes.size == 3) {
                            calendario.set(
                                partes[0].toIntOrNull()
                                    ?: calendario.get(Calendar.YEAR),
                                (partes[1].toIntOrNull()
                                    ?: calendario.get(Calendar.MONTH) + 1) - 1,
                                partes[2].toIntOrNull()
                                    ?: calendario.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                    }

                    // Abre el calendario nativo de Android.
                    DatePickerDialog(
                        contexto,
                        { _, anio, mes, dia ->
                            // Guarda la fecha con formato AAAA-MM-DD.
                            val fechaSeleccionada = String.format(
                                Locale.ROOT,
                                "%04d-%02d-%02d",
                                anio,
                                mes + 1,
                                dia
                            )

                            alSeleccionar(fechaSeleccionada)
                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Text("Elegir")
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