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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyectoaplicaciones.data.local.OrganizacionEntity
import com.example.proyectoaplicaciones.model.OrganizacionUiState
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.OrganizacionViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    usuario: UsuarioLocal,
    alVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()

    // Identifica el perfil conectado.
    val esEjecutivo = usuario.rol.equals(
        "EJECUTIVO_COMERCIAL",
        ignoreCase = true
    )

    val esSupervisor = usuario.rol.equals(
        "SUPERVISOR_COMERCIAL",
        ignoreCase = true
    )

    var mostrarFormulario by remember { mutableStateOf(false) }

    var organizacionAEliminar by remember {
        mutableStateOf<OrganizacionEntity?>(null)
    }

    var mensajeDialogo by remember { mutableStateOf<String?>(null) }
    var cerrarFormularioAlAceptar by remember { mutableStateOf(false) }

    // Escucha los resultados de las operaciones del ViewModel.
    LaunchedEffect(viewModel) {
        viewModel.resultadoOperacion.collectLatest { mensaje ->

            mensajeDialogo = mensaje

            cerrarFormularioAlAceptar =
                mensaje == "Organización registrada exitosamente" ||
                        mensaje == "Organización actualizada exitosamente"
        }
    }

    // Acción común para cerrar el mensaje de confirmación.
    val aceptarResultado = {
        mensajeDialogo = null

        if (cerrarFormularioAlAceptar) {
            mostrarFormulario = false
            viewModel.prepararNuevaOrganizacion()
        }

        cerrarFormularioAlAceptar = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esSupervisor) {
                            "Consultar organizaciones"
                        } else {
                            "Organizaciones"
                        }
                    )
                },
                navigationIcon = {
                    TextButton(onClick = alVolver) {
                        Text("Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            // Solo el Ejecutivo puede registrar organizaciones.
            if (esEjecutivo && !mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        viewModel.prepararNuevaOrganizacion()
                        mostrarFormulario = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva organización"
                    )
                }
            }
        }
    ) { padding ->

        // El formulario solo puede abrirse desde el perfil Ejecutivo.
        if (esEjecutivo && mostrarFormulario) {

            FormularioOrganizacion(
                estado = estado,
                viewModel = viewModel,
                alCancelar = {
                    viewModel.prepararNuevaOrganizacion()
                    mostrarFormulario = false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

        } else {

            if (organizaciones.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay organizaciones registradas.")
                }

            } else {

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
                                "Listado de organizaciones para supervisión"
                            } else {
                                "Organizaciones registradas"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Total: ${organizaciones.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    items(
                        items = organizaciones,
                        key = { it.id }
                    ) { organizacion ->

                        OrganizacionItem(
                            organizacion = organizacion,
                            puedeGestionar = esEjecutivo,
                            alEditar = {
                                if (esEjecutivo) {
                                    viewModel.iniciarEdicion(organizacion)
                                    mostrarFormulario = true
                                }
                            },
                            alEliminar = {
                                if (esEjecutivo) {
                                    organizacionAEliminar = organizacion
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmación antes de eliminar una organización.
    if (esEjecutivo) {
        organizacionAEliminar?.let { organizacion ->

            AlertDialog(
                onDismissRequest = {
                    organizacionAEliminar = null
                },
                title = {
                    Text("Eliminar organización")
                },
                text = {
                    Text(
                        "¿Estás seguro de eliminar ${organizacion.nombre}? " +
                                "También se eliminarán sus contactos asociados."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.eliminarOrganizacion(organizacion)
                            organizacionAEliminar = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            organizacionAEliminar = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    // Ventana de resultado de las operaciones.
    mensajeDialogo?.let { mensaje ->

        val esError = mensaje.contains("error", ignoreCase = true)

        AlertDialog(
            onDismissRequest = aceptarResultado,
            title = {
                Text(
                    if (esError) {
                        "No se pudo completar la operación"
                    } else {
                        "Operación exitosa"
                    }
                )
            },
            text = {
                Text(mensaje)
            },
            confirmButton = {
                TextButton(
                    onClick = aceptarResultado
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun OrganizacionItem(
    organizacion: OrganizacionEntity,
    puedeGestionar: Boolean,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = organizacion.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Rubro: ${organizacion.rubro}")
            Text("Tipo: ${organizacion.tipo}")
            Text("Ubicación: ${organizacion.comuna}, ${organizacion.region}")
            Text("Correo: ${organizacion.correo}")
            Text("Teléfono: ${organizacion.telefono}")

            // Los botones de gestión solo aparecen para el Ejecutivo.
            if (puedeGestionar) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    IconButton(onClick = alEditar) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }

                    IconButton(onClick = alEliminar) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioOrganizacion(
    estado: OrganizacionUiState,
    viewModel: OrganizacionViewModel,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = if (estado.esEdicion) {
                "Editar organización"
            } else {
                "Nueva organización"
            },
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre") },
            isError = estado.errores.nombre != null,
            supportingText = {
                estado.errores.nombre?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.rubro,
            onValueChange = viewModel::onRubroChange,
            label = { Text("Rubro") },
            isError = estado.errores.rubro != null,
            supportingText = {
                estado.errores.rubro?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.tipo,
            onValueChange = viewModel::onTipoChange,
            label = { Text("Tipo de organización") },
            isError = estado.errores.tipo != null,
            supportingText = {
                estado.errores.tipo?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text("Dirección") },
            isError = estado.errores.direccion != null,
            supportingText = {
                estado.errores.direccion?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.comuna,
            onValueChange = viewModel::onComunaChange,
            label = { Text("Comuna") },
            isError = estado.errores.comuna != null,
            supportingText = {
                estado.errores.comuna?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.region,
            onValueChange = viewModel::onRegionChange,
            label = { Text("Región") },
            isError = estado.errores.region != null,
            supportingText = {
                estado.errores.region?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.telefono,
            onValueChange = viewModel::onTelefonoChange,
            label = { Text("Teléfono") },
            isError = estado.errores.telefono != null,
            supportingText = {
                estado.errores.telefono?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = {
                estado.errores.correo?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.descripcion,
            onValueChange = viewModel::onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                onClick = alCancelar,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    viewModel.guardarOrganizacion()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}