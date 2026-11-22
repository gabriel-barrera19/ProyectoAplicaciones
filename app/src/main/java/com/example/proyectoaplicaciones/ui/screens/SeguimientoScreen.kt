package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.viewmodel.SeguimientoViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoScreen(
    viewModel: SeguimientoViewModel,
    oportunidadId: Int,
    nombreOportunidad: String,
    usuario: UsuarioLocal,
    alVolver: () -> Unit
) {
    val seguimientos by viewModel.seguimientos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val esEjecutivo = usuario.rol.equals(
        "EJECUTIVO_COMERCIAL",
        ignoreCase = true
    )

    var fecha by remember { mutableStateOf("") }
    var tipoGestion by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    androidx.compose.runtime.LaunchedEffect(oportunidadId) {
        viewModel.cargarSeguimientos(oportunidadId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Seguimientos")
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = nombreOportunidad,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Historial de gestiones comerciales",
                style = MaterialTheme.typography.titleMedium
            )

            if (esEjecutivo) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "Registrar nueva gestión",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = fecha,
                            onValueChange = { fecha = it },
                            label = { Text("Fecha (AAAA-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = tipoGestion,
                            onValueChange = { tipoGestion = it },
                            label = { Text("Tipo de gestión") },
                            placeholder = {
                                Text("Ej. Llamada, reunión o correo")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = resultado,
                            onValueChange = { resultado = it },
                            label = { Text("Resultado") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = observaciones,
                            onValueChange = { observaciones = it },
                            label = { Text("Observaciones") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Button(
                            onClick = {
                                viewModel.registrarSeguimiento(
                                    oportunidadId = oportunidadId,
                                    fecha = fecha,
                                    tipoGestion = tipoGestion,
                                    resultado = resultado,
                                    observaciones = observaciones
                                )

                                fecha = ""
                                tipoGestion = ""
                                resultado = ""
                                observaciones = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar seguimiento")
                        }
                    }
                }
            }

            mensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Historial registrado (${seguimientos.size})",
                style = MaterialTheme.typography.titleMedium
            )

            if (seguimientos.isEmpty()) {
                Text("Todavía no hay seguimientos registrados.")
            } else {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = seguimientos,
                        key = { it.id }
                    ) { seguimiento ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = seguimiento.tipoGestion,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Fecha: ${seguimiento.fecha}")
                                Text("Resultado: ${seguimiento.resultado}")

                                Text(
                                    text = "Observaciones: ${
                                        seguimiento.observaciones.ifBlank {
                                            "Sin observaciones"
                                        }
                                    }"
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = alVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    }
}