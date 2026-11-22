package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectoaplicaciones.model.UsuarioLocal

/**
 * Menú principal de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(
    usuario: UsuarioLocal,
    alVerOportunidades: () -> Unit,
    alVerOrganizaciones: () -> Unit,
    alVerContactos: () -> Unit,
    alCerrarSesion: () -> Unit,
    alAbrirLectorQR: () -> Unit
) {
    var menuAbierto by remember {
        mutableStateOf(false)
    }

    var mostrarContenido by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        mostrarContenido = true
    }

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
                                Text("Gestión de organizaciones")
                            },
                            onClick = {
                                menuAbierto = false
                                alVerOrganizaciones()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Gestión de contactos")
                            },
                            onClick = {
                                menuAbierto = false
                                alVerContactos()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Leer código QR")
                            },
                            onClick = {
                                menuAbierto = false
                                alAbrirLectorQR()
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

        AnimatedVisibility(
            visible = mostrarContenido,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 600)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 600),
                initialOffsetY = { altura ->
                    altura / 8
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bienvenido, ${usuario.nombre}",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Perfil: ${usuario.rol.replace("_", " ")}",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = usuario.correo,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = alVerOportunidades,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gestionar oportunidades")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = alVerOrganizaciones,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gestionar organizaciones")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = alVerContactos,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gestionar contactos")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = alAbrirLectorQR,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Leer código QR")
                }
            }
        }
    }
}