package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

/**
 * Muestra las oportunidades almacenadas en Room.
 * Permite registrar, editar y eliminar oportunidades.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    viewModel: OportunidadViewModel,
    alRegistrarOportunidad: () -> Unit,
    alEditarOportunidad: (OportunidadEntity) -> Unit
) {
    // Observa la lista de oportunidades del ViewModel.
    val oportunidades by viewModel.oportunidades.collectAsState()

    // Guarda la oportunidad que el usuario quiere eliminar.
    var oportunidadPorEliminar by remember {
        mutableStateOf<OportunidadEntity?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Gestión de Oportunidades")
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Abre el formulario para registrar una oportunidad nueva.
            Button(
                onClick = alRegistrarOportunidad,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar oportunidad")
            }

            if (oportunidades.isEmpty()) {

                // Mensaje mostrado cuando no hay registros.
                Text(
                    text = "Todavía no hay oportunidades registradas.",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else {

                // Lista desplazable de oportunidades.
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = oportunidades,
                        key = { oportunidad -> oportunidad.id }
                    ) { oportunidad ->

                        // Tarjeta con los datos de cada oportunidad.
                        Card(
                            modifier = Modifier.fillMaxWidth()
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

                                // Botones para modificar o eliminar el registro.
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
                                            oportunidadPorEliminar = oportunidad
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

    // Pide confirmación antes de borrar definitivamente.
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
                    "¿Seguro que deseas eliminar " +
                            "\"${oportunidad.nombre}\"? Esta acción no se puede deshacer."
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