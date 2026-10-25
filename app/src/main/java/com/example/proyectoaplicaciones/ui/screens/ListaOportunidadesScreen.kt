package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

/**
 * Muestra las oportunidades almacenadas en Room.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    viewModel: OportunidadViewModel,
    alRegistrarOportunidad: () -> Unit
) {
    // Observa la lista de oportunidades del ViewModel.
    val oportunidades by viewModel.oportunidades.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Oportunidades") }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Respeta el espacio ocupado por la barra superior.
                .padding(paddingInterno)
                // Mantiene los márgenes laterales y verticales.
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Permite abrir el formulario de registro.
            Button(
                onClick = alRegistrarOportunidad,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar oportunidad")
            }

            if (oportunidades.isEmpty()) {

                // Mensaje mostrado cuando aún no hay registros.
                Text(
                    text = "Todavía no hay oportunidades registradas.",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else {

                // Lista que se actualiza con los datos observados.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                    // La lista gestiona su propio desplazamiento.
                    ,
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = oportunidades,
                        key = { oportunidad -> oportunidad.id }
                    ) { oportunidad ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = oportunidad.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Tipo: ${oportunidad.tipo}")
                                Text("Prioridad: ${oportunidad.prioridad}")
                                Text("Estado: ${oportunidad.estado}")
                            }
                        }
                    }
                }
            }
        }
    }
}