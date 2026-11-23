package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.data.local.ContactoEntity
import com.example.proyectoaplicaciones.data.local.OrganizacionEntity
import com.example.proyectoaplicaciones.model.ContactoUiState
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.ContactoViewModel
import com.example.proyectoaplicaciones.viewmodel.OrganizacionViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(
    contactoViewModel: ContactoViewModel,
    organizacionViewModel: OrganizacionViewModel,
    usuario: UsuarioLocal,
    alVolver: () -> Unit,
    alVerOportunidades: () -> Unit,
    alVerOrganizaciones: () -> Unit,
    alAbrirLectorQR: () -> Unit,
    alCerrarSesion: () -> Unit,
    alVerEventos: (() -> Unit)? = null
) {
    val estado by contactoViewModel.estado.collectAsState()
    val contactos by contactoViewModel.contactos.collectAsState()
    val contactosFiltrados by contactoViewModel.contactosFiltrados.collectAsState()
    val organizaciones by organizacionViewModel.organizaciones.collectAsState()

    val esEjecutivo = usuario.rol.equals(
        "EJECUTIVO_COMERCIAL",
        ignoreCase = true
    )

    val esSupervisor = usuario.rol.equals(
        "SUPERVISOR_COMERCIAL",
        ignoreCase = true
    )

    var mostrarFormulario by remember { mutableStateOf(false) }
    var contactoEliminar by remember { mutableStateOf<ContactoEntity?>(null) }
    var busqueda by remember { mutableStateOf("") }
    var filtroOrganizacionId by remember { mutableStateOf<Int?>(null) }
    var filtroActivo by remember { mutableStateOf(false) }

    var mensajeDialogo by remember { mutableStateOf<String?>(null) }
    var cerrarFormularioAlAceptar by remember { mutableStateOf(false) }

    // Escucha los resultados de guardar, actualizar y eliminar.
    LaunchedEffect(contactoViewModel) {
        contactoViewModel.resultadoOperacion.collect { mensaje ->
            mensajeDialogo = mensaje

            cerrarFormularioAlAceptar =
                mensaje == "Contacto registrado exitosamente" ||
                        mensaje == "Contacto actualizado exitosamente"
        }
    }

    // Cierra el formulario después de un guardado exitoso.
    val aceptarResultado = {
        mensajeDialogo = null

        if (cerrarFormularioAlAceptar) {
            mostrarFormulario = false
            contactoViewModel.prepararNuevoContacto()
        }

        cerrarFormularioAlAceptar = false
    }

    val listaVisible = if (filtroActivo) {
        contactosFiltrados
    } else {
        contactos
    }

    val listaFinal = listaVisible.filter { contacto ->
        val coincideBusqueda = contacto.nombre.contains(
            busqueda.trim(),
            ignoreCase = true
        )

        val coincideOrganizacion =
            filtroOrganizacionId == null ||
                    contacto.organizacionId == filtroOrganizacionId

        coincideBusqueda && coincideOrganizacion
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (esSupervisor) {
                            "Consultar contactos"
                        } else {
                            "Gestión de contactos"
                        }
                    )
                },
                navigationIcon = {
                    MenuAplicacion(
                        usuario = usuario,
                        alIrMenuPrincipal = alVolver,
                        alVerOportunidades = alVerOportunidades,
                        alVerOrganizaciones = alVerOrganizaciones,
                        alVerContactos = null,
                        alAbrirLectorQR = alAbrirLectorQR,
                        alCerrarSesion = alCerrarSesion,
                        mostrarOportunidades = true,
                        alVerEventos = alVerEventos
                    )
                }
            )
        },
        floatingActionButton = {
            // Solo el Ejecutivo puede registrar contactos.
            if (esEjecutivo && !mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        contactoViewModel.prepararNuevoContacto()
                        mostrarFormulario = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo contacto"
                    )
                }
            }
        }
    ) { padding ->

        if (esEjecutivo && mostrarFormulario) {

            FormularioContacto(
                estado = estado,
                organizaciones = organizaciones,
                onNombreChange = contactoViewModel::onNombreChange,
                onCargoChange = contactoViewModel::onCargoChange,
                onCorreoChange = contactoViewModel::onCorreoChange,
                onTelefonoChange = contactoViewModel::onTelefonoChange,
                onOrganizacionChange = contactoViewModel::onOrganizacionChange,
                onGuardar = contactoViewModel::guardarContacto,
                onCancelar = {
                    contactoViewModel.prepararNuevoContacto()
                    mostrarFormulario = false
                }
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Text(
                    text = if (esSupervisor) {
                        "Listado de contactos para supervisión"
                    } else {
                        "Contactos registrados"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { valor ->
                        busqueda = valor

                        if (valor.isNotBlank()) {
                            contactoViewModel.buscarContactos(valor)
                            filtroActivo = true
                        } else if (filtroOrganizacionId == null) {
                            contactoViewModel.limpiarFiltro()
                            filtroActivo = false
                        } else {
                            contactoViewModel.obtenerContactosPorOrganizacion(
                                filtroOrganizacionId!!
                            )
                            filtroActivo = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Buscar contacto por nombre")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                var desplegarFiltro by remember {
                    mutableStateOf(false)
                }

                Column {
                    OutlinedTextField(
                        value = filtroOrganizacionId?.let { id ->
                            organizaciones.find { it.id == id }?.nombre
                        } ?: "Todas las organizaciones",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = {
                            Text("Filtrar por organización")
                        }
                    )

                    TextButton(
                        onClick = {
                            desplegarFiltro = true
                        }
                    ) {
                        Text("Seleccionar organización")
                    }

                    DropdownMenu(
                        expanded = desplegarFiltro,
                        onDismissRequest = {
                            desplegarFiltro = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Todas las organizaciones")
                            },
                            onClick = {
                                filtroOrganizacionId = null
                                desplegarFiltro = false

                                if (busqueda.isNotBlank()) {
                                    contactoViewModel.buscarContactos(busqueda)
                                    filtroActivo = true
                                } else {
                                    contactoViewModel.limpiarFiltro()
                                    filtroActivo = false
                                }
                            }
                        )

                        organizaciones.forEach { organizacion ->
                            DropdownMenuItem(
                                text = {
                                    Text(organizacion.nombre)
                                },
                                onClick = {
                                    filtroOrganizacionId = organizacion.id
                                    desplegarFiltro = false

                                    contactoViewModel.obtenerContactosPorOrganizacion(
                                        organizacion.id
                                    )
                                    filtroActivo = true
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (listaFinal.isEmpty()) {

                    Text(
                        text = if (organizaciones.isEmpty()) {
                            "Primero debes registrar una organización."
                        } else {
                            "No hay contactos para mostrar."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp)
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = listaFinal,
                            key = { it.id }
                        ) { contacto ->

                            ContactoCard(
                                contacto = contacto,
                                organizaciones = organizaciones,
                                puedeGestionar = esEjecutivo,
                                onEditar = {
                                    if (esEjecutivo) {
                                        contactoViewModel.iniciarEdicion(contacto)
                                        mostrarFormulario = true
                                    }
                                },
                                onEliminar = {
                                    if (esEjecutivo) {
                                        contactoEliminar = contacto
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmación antes de eliminar.
    if (esEjecutivo) {
        contactoEliminar?.let { contacto ->
            AlertDialog(
                onDismissRequest = {
                    contactoEliminar = null
                },
                title = {
                    Text("Eliminar contacto")
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar a ${contacto.nombre}?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            contactoViewModel.eliminarContacto(contacto)
                            contactoEliminar = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            contactoEliminar = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    // Ventana de confirmación del resultado.
    if (esEjecutivo) {
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
}

@Composable
private fun ContactoCard(
    contacto: ContactoEntity,
    organizaciones: List<OrganizacionEntity>,
    puedeGestionar: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val organizacion = organizaciones.find {
        it.id == contacto.organizacionId
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = contacto.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Cargo: ${contacto.cargo}")
            Text("Correo: ${contacto.correo}")
            Text("Teléfono: ${contacto.telefono}")
            Text("Organización: ${organizacion?.nombre ?: "No disponible"}")

            // Acciones exclusivas del Ejecutivo.
            if (puedeGestionar) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEditar) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar contacto"
                        )
                    }

                    IconButton(onClick = onEliminar) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar contacto"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioContacto(
    estado: ContactoUiState,
    organizaciones: List<OrganizacionEntity>,
    onNombreChange: (String) -> Unit,
    onCargoChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onOrganizacionChange: (Int) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var desplegarOrganizaciones by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                if (estado.esEdicion) {
                    "Editar contacto"
                } else {
                    "Registrar contacto"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = onNombreChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre")
                    },
                    isError = estado.errores.nombre != null,
                    supportingText = {
                        estado.errores.nombre?.let { Text(it) }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = estado.cargo,
                    onValueChange = onCargoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Cargo")
                    },
                    isError = estado.errores.cargo != null,
                    supportingText = {
                        estado.errores.cargo?.let { Text(it) }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = onCorreoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Correo electrónico")
                    },
                    isError = estado.errores.correo != null,
                    supportingText = {
                        estado.errores.correo?.let { Text(it) }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = estado.telefono,
                    onValueChange = onTelefonoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Teléfono")
                    },
                    isError = estado.errores.telefono != null,
                    supportingText = {
                        estado.errores.telefono?.let { Text(it) }
                    },
                    singleLine = true
                )

                Column {
                    OutlinedTextField(
                        value = organizaciones.find {
                            it.id == estado.organizacionId
                        }?.nombre ?: "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = {
                            Text("Organización")
                        },
                        isError = estado.errores.organizacionId != null,
                        supportingText = {
                            estado.errores.organizacionId?.let { Text(it) }
                        }
                    )

                    TextButton(
                        onClick = {
                            desplegarOrganizaciones = true
                        }
                    ) {
                        Text("Seleccionar organización")
                    }

                    DropdownMenu(
                        expanded = desplegarOrganizaciones,
                        onDismissRequest = {
                            desplegarOrganizaciones = false
                        }
                    ) {
                        organizaciones.forEach { organizacion ->
                            DropdownMenuItem(
                                text = {
                                    Text(organizacion.nombre)
                                },
                                onClick = {
                                    onOrganizacionChange(organizacion.id)
                                    desplegarOrganizaciones = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}