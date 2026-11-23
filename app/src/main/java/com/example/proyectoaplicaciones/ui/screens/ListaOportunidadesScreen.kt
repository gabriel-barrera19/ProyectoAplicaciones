package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.data.local.OportunidadEntity
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    viewModel: OportunidadViewModel,
    usuario: UsuarioLocal,
    alRegistrarOportunidad: () -> Unit,
    alEditarOportunidad: (OportunidadEntity) -> Unit,
    alVerSeguimientos: (OportunidadEntity) -> Unit,
    alIrMenuPrincipal: () -> Unit,
    alCerrarSesion: () -> Unit,
    alVerOrganizaciones: (() -> Unit)? = null,
    alVerContactos: (() -> Unit)? = null,
    alAbrirLectorQR: (() -> Unit)? = null,
    alVerEventos: (() -> Unit)? = null
) {
    val oportunidades by viewModel.oportunidades.collectAsState()

    var oportunidadPorEliminar by remember {
        mutableStateOf<OportunidadEntity?>(null)
    }

    var oportunidadSeleccionada by remember {
        mutableStateOf<OportunidadEntity?>(null)
    }

    val esEjecutivo = usuario.rol.equals(
        "EJECUTIVO_COMERCIAL",
        ignoreCase = true
    )

    val esSupervisor = usuario.rol.equals(
        "SUPERVISOR_COMERCIAL",
        ignoreCase = true
    )

    val mostrandoDetalle =
        esSupervisor && oportunidadSeleccionada != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            mostrandoDetalle ->
                                "Detalle de oportunidad"

                            esSupervisor ->
                                "Consultar oportunidades"

                            else ->
                                "Gestión de oportunidades"
                        }
                    )
                },
                navigationIcon = {
                    MenuAplicacion(
                        usuario = usuario,
                        alIrMenuPrincipal = alIrMenuPrincipal,
                        alVerOportunidades = {},
                        alCerrarSesion = alCerrarSesion,
                        alVerOrganizaciones = alVerOrganizaciones,
                        alVerContactos = alVerContactos,
                        alAbrirLectorQR = alAbrirLectorQR,
                        mostrarOportunidades = false,
                        alVerEventos = alVerEventos
                    )
                }
            )
        }
    ) { paddingInterno ->

        val modifierContenido = if (mostrandoDetalle) {
            Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        }

        Column(
            modifier = modifierContenido,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            if (mostrandoDetalle) {

                val oportunidad = oportunidadSeleccionada!!

                OutlinedButton(
                    onClick = {
                        oportunidadSeleccionada = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al listado")
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = oportunidad.nombre,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "Identificador: ${oportunidad.id}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = oportunidad.descripcion.ifBlank {
                                "Sin descripción registrada"
                            }
                        )

                        Text(
                            text = "Información comercial",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Tipo: ${oportunidad.tipo}")
                        Text("Fuente: ${oportunidad.fuente}")
                        Text("Prioridad: ${oportunidad.prioridad}")
                        Text("Potencial comercial: ${oportunidad.potencialComercial}")

                        Text(
                            text = "Estado actual",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(text = oportunidad.estado)

                        Text(
                            text = "Fecha de la oportunidad",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = oportunidad.fecha.ifBlank {
                                "Sin fecha registrada"
                            }
                        )

                        Text(
                            text = "Observaciones",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = oportunidad.observaciones.ifBlank {
                                "Sin observaciones registradas"
                            }
                        )

                        Button(
                            onClick = {
                                alVerSeguimientos(oportunidad)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver seguimientos")
                        }
                    }
                }

            } else {

                if (esEjecutivo) {
                    Button(
                        onClick = alRegistrarOportunidad,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar oportunidad")
                    }
                }

                if (esSupervisor) {
                    Text(
                        text = "Listado de oportunidades para supervisión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (oportunidades.isEmpty()) {

                    Text(
                        text = "Todavía no hay oportunidades registradas.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(
                            items = oportunidades,
                            key = { oportunidad -> oportunidad.id }
                        ) { oportunidad ->

                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 500
                                    )
                                ) + slideInVertically(
                                    animationSpec = tween(
                                        durationMillis = 500
                                    ),
                                    initialOffsetY = { altura ->
                                        altura / 8
                                    }
                                )
                            ) {

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (esSupervisor) {
                                                Modifier.clickable {
                                                    oportunidadSeleccionada =
                                                        oportunidad
                                                }
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        Text(
                                            text = oportunidad.nombre,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text("Tipo: ${oportunidad.tipo}")
                                        Text("Prioridad: ${oportunidad.prioridad}")
                                        Text("Estado: ${oportunidad.estado}")

                                        if (esSupervisor) {
                                            Text(
                                                text = "Pulsa para ver toda la información",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                alVerSeguimientos(oportunidad)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Ver seguimientos")
                                        }

                                        if (esEjecutivo) {

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {

                                                Button(
                                                    onClick = {
                                                        alEditarOportunidad(oportunidad)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Editar")
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        oportunidadPorEliminar =
                                                            oportunidad
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Eliminar")
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
        }
    }

    oportunidadPorEliminar?.let { oportunidad ->

        AlertDialog(
            onDismissRequest = {
                oportunidadPorEliminar = null
            },
            title = {
                Text("Eliminar oportunidad")
            },
            text = {
                Text(
                    "¿Seguro que deseas eliminar \"${oportunidad.nombre}\"? " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarOportunidad(oportunidad)
                        oportunidadPorEliminar = null
                    }
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        oportunidadPorEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
