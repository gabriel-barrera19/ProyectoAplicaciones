package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api

/**
 * Menú principal de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(
    alVerOportunidades: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    // Controla si el menú desplegable está abierto.
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Menú principal")
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            menuAbierto = true
                        }
                    ) {
                        Text(
                            text = "☰",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = {
                            menuAbierto = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Gestión de oportunidades")
                            },
                            onClick = {
                                menuAbierto = false
                                alVerOportunidades()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Cerrar sesión")
                            },
                            onClick = {
                                menuAbierto = false
                                alCerrarSesion()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingInterno ->

        // Contenido central del menú.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona una opción para continuar.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = alVerOportunidades,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar oportunidades")
            }
        }
    }
}