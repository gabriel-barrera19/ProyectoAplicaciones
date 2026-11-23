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
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import java.util.Calendar
import java.util.Locale

// Pantalla para registrar o editar una oportunidad comercial.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    viewModel: OportunidadViewModel,
    usuario: UsuarioLocal,
    alVolver: () -> Unit,
    alIrMenuPrincipal: () -> Unit,
    alVerOportunidades: () -> Unit,
    alCerrarSesion: () -> Unit,
    alVerOrganizaciones: (() -> Unit)? = null,
    alVerContactos: (() -> Unit)? = null,
    alAbrirLectorQR: (() -> Unit)? = null
) {
    val estado by viewModel.estado.collectAsState()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        mostrarFormulario = true
    }

    var mensajeConfirmacion by remember {
        mutableStateOf<String?>(null)
    }

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
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la lista"
                        )
                    }
                },
                actions = {
                    MenuAplicacion(
                        usuario = usuario,
                        alIrMenuPrincipal = alIrMenuPrincipal,
                        alVerOportunidades = alVerOportunidades,
                        alCerrarSesion = alCerrarSesion,
                        alVerOrganizaciones = alVerOrganizaciones,
                        alVerContactos = alVerContactos,
                        alAbrirLectorQR = alAbrirLectorQR,
                        mostrarOportunidades = false
                    )
                }
            )
        }
    ) { paddingInterno ->

        AnimatedVisibility(
            visible = mostrarFormulario,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 600)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 600),
                initialOffsetY = { altura -> altura / 10 }
            )
        ) {
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
                CampoTexto(
                    etiqueta = "Nombre de la oportunidad",
                    valor = estado.nombre,
                    error = estado.errores.nombre,
                    alCambiar = viewModel::onNombreChange
                )

                CampoTexto(
                    etiqueta = "Descripción",
                    valor = estado.descripcion,
                    error = estado.errores.descripcion,
                    alCambiar = viewModel::onDescripcionChange,
                    unaLinea = false
                )

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

                CampoTexto(
                    etiqueta = "Fuente de información",
                    valor = estado.fuente,
                    error = estado.errores.fuente,
                    alCambiar = viewModel::onFuenteChange
                )

                CampoFecha(
                    fecha = estado.fecha,
                    alSeleccionar = viewModel::onFechaChange
                )

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

                CampoTexto(
                    etiqueta = "Observaciones (opcional)",
                    valor = estado.observaciones,
                    error = null,
                    alCambiar = viewModel::onObservacionesChange,
                    unaLinea = false
                )

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
    val contexto = LocalContext.current

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
                    val calendario = Calendar.getInstance()

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

                    DatePickerDialog(
                        contexto,
                        { _, anio, mes, dia ->
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