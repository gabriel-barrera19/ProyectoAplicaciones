package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.data.local.EventoEntity
import com.example.proyectoaplicaciones.data.local.OrganizacionEntity
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.EventoViewModel
import com.example.proyectoaplicaciones.viewmodel.OrganizacionViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoScreen(
    eventoViewModel: EventoViewModel,
    organizacionViewModel: OrganizacionViewModel,
    usuario: UsuarioLocal,
    alVolver: () -> Unit,
    alVerOportunidades: () -> Unit,
    alVerOrganizaciones: () -> Unit,
    alVerContactos: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val eventos by eventoViewModel.eventos.collectAsState()
    val organizaciones by organizacionViewModel.organizaciones.collectAsState()
    val estadoFormulario by eventoViewModel.estado.collectAsState()

    val esEjecutivo = usuario.rol.equals("EJECUTIVO_COMERCIAL", ignoreCase = true)
    val esSupervisor = usuario.rol.equals("SUPERVISOR_COMERCIAL", ignoreCase = true)

    var mostrarFormulario by remember { mutableStateOf(false) }
    var eventoEditando by remember { mutableStateOf<EventoEntity?>(null) }
    var eventoSeleccionado by remember { mutableStateOf<EventoEntity?>(null) }
    var eventoEliminar by remember { mutableStateOf<EventoEntity?>(null) }
    var mensajeDialogo by remember { mutableStateOf<String?>(null) }

    val mostrandoDetalle = esSupervisor && eventoSeleccionado != null

    LaunchedEffect(eventoViewModel) {
        eventoViewModel.mensaje.collect { mensaje ->
            if (mensaje != null) {
                mensajeDialogo = mensaje
                if (mensaje.contains("correctamente", ignoreCase = true)) {
                    mostrarFormulario = false
                    eventoEditando = null
                }
                eventoViewModel.limpiarMensaje()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            mostrandoDetalle -> "Detalle del evento"
                            mostrarFormulario && eventoEditando != null -> "Editar evento"
                            mostrarFormulario -> "Nuevo evento"
                            esSupervisor -> "Consultar eventos"
                            else -> "Gestión de eventos"
                        }
                    )
                },
                navigationIcon = {
                    MenuAplicacion(
                        usuario = usuario,
                        alIrMenuPrincipal = alVolver,
                        alVerOportunidades = alVerOportunidades,
                        alVerOrganizaciones = alVerOrganizaciones,
                        alVerContactos = alVerContactos,
                        alAbrirLectorQR = null,
                        alCerrarSesion = alCerrarSesion,
                        mostrarOportunidades = true
                    )
                }
            )
        },
        floatingActionButton = {
            if (esEjecutivo && !mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        eventoEditando = null
                        eventoViewModel.prepararNuevoEvento()
                        mostrarFormulario = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo evento"
                    )
                }
            }
        }
    ) { padding ->

        when {
            mostrarFormulario && esEjecutivo -> {
                FormularioEvento(
                    estado = estadoFormulario,
                    viewModel = eventoViewModel,
                    organizaciones = organizaciones,
                    alCancelar = {
                        mostrarFormulario = false
                        eventoEditando = null
                    },
                    alGuardar = {
                        eventoViewModel.guardarEvento()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            mostrandoDetalle -> {
                val evento = eventoSeleccionado!!
                val organizacion = organizaciones.find {
                    it.id == evento.organizacionId
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedButton(
                        onClick = { eventoSeleccionado = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al listado")
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = evento.nombre,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Identificador: ${evento.id}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text("Información general", style = MaterialTheme.typography.titleMedium)
                            Text("Tipo: ${evento.tipo}")
                            Text("Descripción: ${evento.descripcion.ifBlank { "Sin descripción registrada" }}")
                            Text("Organización responsable: ${organizacion?.nombre ?: "Sin organización asignada"}")

                            HorizontalDivider()

                            Text("Fechas y horario", style = MaterialTheme.typography.titleMedium)
                            Text("Fecha de inicio: ${evento.fechaInicio}")
                            Text("Fecha de término: ${evento.fechaTermino}")
                            Text("Horario: ${evento.horario.ifBlank { "No especificado" }}")

                            HorizontalDivider()

                            Text("Ubicación", style = MaterialTheme.typography.titleMedium)
                            Text("Dirección o recinto: ${evento.ubicacion}")
                            Text("Comuna: ${evento.comuna}")
                            Text("Región: ${evento.region}")

                            HorizontalDivider()

                            Text("Información del público", style = MaterialTheme.typography.titleMedium)
                            Text("Público objetivo: ${evento.publicoObjetivo.ifBlank { "No especificado" }}")
                            Text("Público esperado: ${evento.publicoEsperado.ifBlank { "No especificado" }}")
                            Text("Cantidad estimada de asistentes: ${evento.cantidadEstimadaAsistentes?.toString() ?: "No especificada"}")

                            HorizontalDivider()

                            Text("Condiciones de participación", style = MaterialTheme.typography.titleMedium)
                            Text("Condiciones: ${evento.condicionesParticipacion.ifBlank { "No especificadas" }}")
                            Text("Requisitos: ${evento.requisitos.ifBlank { "No especificados" }}")
                            Text("Costos referenciales: ${evento.costosReferenciales.ifBlank { "No especificados" }}")
                            Text("Espacios disponibles: ${evento.espaciosDisponibles.ifBlank { "No especificados" }}")

                            HorizontalDivider()

                            Text("Observaciones", style = MaterialTheme.typography.titleMedium)
                            Text(evento.observaciones.ifBlank { "Sin observaciones registradas" })
                        }
                    }

                    OutlinedButton(
                        onClick = { eventoSeleccionado = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al listado")
                    }
                }
            }

            eventos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay eventos registrados.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = if (esSupervisor) {
                                "Listado de eventos para supervisión"
                            } else {
                                "Eventos registrados"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(items = eventos, key = { it.id }) { evento ->
                        val organizacion = organizaciones.find {
                            it.id == evento.organizacionId
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = evento.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Tipo: ${evento.tipo}")
                                Text("Inicio: ${evento.fechaInicio}")
                                Text("Término: ${evento.fechaTermino}")
                                Text("Ubicación: ${evento.ubicacion}")
                                Text("Comuna: ${evento.comuna}")
                                Text("Organización: ${organizacion?.nombre ?: "Sin organización asignada"}")

                                if (esSupervisor) {
                                    Button(
                                        onClick = { eventoSeleccionado = evento },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Ver detalle completo")
                                    }
                                }

                                if (esEjecutivo) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = {
                                                eventoEditando = evento
                                                eventoViewModel.iniciarEdicion(evento)
                                                mostrarFormulario = true
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Editar evento"
                                            )
                                        }
                                        IconButton(
                                            onClick = { eventoEliminar = evento }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar evento"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    eventoEliminar?.let { evento ->
        AlertDialog(
            onDismissRequest = { eventoEliminar = null },
            title = { Text("Eliminar evento") },
            text = { Text("¿Deseas eliminar el evento ${evento.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventoViewModel.eliminarEvento(evento)
                        eventoEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventoEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    mensajeDialogo?.let { mensaje ->
        AlertDialog(
            onDismissRequest = { mensajeDialogo = null },
            title = { Text("Eventos") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = { mensajeDialogo = null }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioEvento(
    estado: com.example.proyectoaplicaciones.model.EventoUiState,
    viewModel: EventoViewModel,
    organizaciones: List<OrganizacionEntity>,
    alCancelar: () -> Unit,
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var desplegarOrganizaciones by remember { mutableStateOf(false) }
    val organizacionSeleccionada = organizaciones.find { it.id == estado.organizacionId }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (estado.esEdicion) "Modificar información del evento" else "Registrar nuevo evento",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = estado.nombre, onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre del evento *") },
            isError = estado.errores.nombre != null,
            supportingText = { estado.errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = estado.tipo, onValueChange = viewModel::onTipoChange,
            label = { Text("Tipo de evento *") },
            isError = estado.errores.tipo != null,
            supportingText = { estado.errores.tipo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = estado.descripcion, onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth()
        )

        Text("Organización responsable", style = MaterialTheme.typography.titleSmall)
        ExposedDropdownMenuBox(
            expanded = desplegarOrganizaciones,
            onExpandedChange = { desplegarOrganizaciones = !desplegarOrganizaciones }
        ) {
            OutlinedTextField(
                value = organizacionSeleccionada?.nombre ?: "Sin organización asignada",
                onValueChange = {}, readOnly = true,
                label = { Text("Seleccionar organización") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarOrganizaciones) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = desplegarOrganizaciones,
                onDismissRequest = { desplegarOrganizaciones = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sin organización asignada") },
                    onClick = {
                        viewModel.onOrganizacionChange(null)
                        desplegarOrganizaciones = false
                    }
                )
                organizaciones.forEach { organizacion ->
                    DropdownMenuItem(
                        text = { Text(organizacion.nombre) },
                        onClick = {
                            viewModel.onOrganizacionChange(organizacion.id)
                            desplegarOrganizaciones = false
                        }
                    )
                }
            }
        }

        Text("Fechas y horario", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = estado.fechaInicio, onValueChange = viewModel::onFechaInicioChange,
            label = { Text("Fecha de inicio * (DD/MM/AAAA)") },
            isError = estado.errores.fechaInicio != null || estado.errores.fechas != null,
            supportingText = {
                estado.errores.fechaInicio?.let { Text(it) } ?: estado.errores.fechas?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = estado.fechaTermino, onValueChange = viewModel::onFechaTerminoChange,
            label = { Text("Fecha de término * (DD/MM/AAAA)") },
            isError = estado.errores.fechaTermino != null || estado.errores.fechas != null,
            supportingText = {
                estado.errores.fechaTermino?.let { Text(it) } ?: estado.errores.fechas?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = estado.horario, onValueChange = viewModel::onHorarioChange,
            label = { Text("Horario") }, modifier = Modifier.fillMaxWidth()
        )

        Text("Ubicación", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = estado.ubicacion, onValueChange = viewModel::onUbicacionChange,
            label = { Text("Dirección o ubicación *") },
            isError = estado.errores.ubicacion != null,
            supportingText = { estado.errores.ubicacion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.comuna, onValueChange = viewModel::onComunaChange,
            label = { Text("Comuna *") },
            isError = estado.errores.comuna != null,
            supportingText = { estado.errores.comuna?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.region, onValueChange = viewModel::onRegionChange,
            label = { Text("Región *") },
            isError = estado.errores.region != null,
            supportingText = { estado.errores.region?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Información del público", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = estado.publicoObjetivo, onValueChange = viewModel::onPublicoObjetivoChange,
            label = { Text("Público objetivo") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.publicoEsperado, onValueChange = viewModel::onPublicoEsperadoChange,
            label = { Text("Público esperado") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.cantidadEstimadaAsistentes, onValueChange = viewModel::onCantidadAsistentesChange,
            label = { Text("Cantidad estimada de asistentes") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Text("Condiciones de participación", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = estado.condicionesParticipacion, onValueChange = viewModel::onCondicionesChange,
            label = { Text("Condiciones") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.requisitos, onValueChange = viewModel::onRequisitosChange,
            label = { Text("Requisitos") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.costosReferenciales, onValueChange = viewModel::onCostosChange,
            label = { Text("Costos referenciales") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.espaciosDisponibles, onValueChange = viewModel::onEspaciosChange,
            label = { Text("Espacios disponibles") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.observaciones, onValueChange = viewModel::onObservacionesChange,
            label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = alCancelar, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Button(onClick = alGuardar, modifier = Modifier.weight(1f)) {
                Text("Guardar")
            }
        }
    }
}