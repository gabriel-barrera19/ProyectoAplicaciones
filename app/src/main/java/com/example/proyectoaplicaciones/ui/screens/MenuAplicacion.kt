package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.example.proyectoaplicaciones.model.UsuarioLocal

@Composable
fun MenuAplicacion(
    usuario: UsuarioLocal,
    alIrMenuPrincipal: () -> Unit,
    alVerOportunidades: () -> Unit,
    alCerrarSesion: () -> Unit,
    alVerOrganizaciones: (() -> Unit)? = null,
    alVerContactos: (() -> Unit)? = null,
    alAbrirLectorQR: (() -> Unit)? = null,
    mostrarOportunidades: Boolean = true
) {
    var menuAbierto by remember {
        mutableStateOf(false)
    }

    val esSupervisor = usuario.rol.equals(
        "SUPERVISOR_COMERCIAL",
        ignoreCase = true
    )

    val tituloOportunidades = if (esSupervisor) {
        "Consultar oportunidades"
    } else {
        "Gestionar oportunidades"
    }

    IconButton(
        onClick = {
            menuAbierto = true
        }
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Abrir menú"
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
                Text("Menú principal")
            },
            onClick = {
                menuAbierto = false
                alIrMenuPrincipal()
            }
        )

        if (mostrarOportunidades) {
            DropdownMenuItem(
                text = {
                    Text(tituloOportunidades)
                },
                onClick = {
                    menuAbierto = false
                    alVerOportunidades()
                }
            )
        }

        if (alVerOrganizaciones != null) {
            DropdownMenuItem(
                text = {
                    Text(
                        if (esSupervisor) {
                            "Consultar organizaciones"
                        } else {
                            "Gestionar organizaciones"
                        }
                    )
                },
                onClick = {
                    menuAbierto = false
                    alVerOrganizaciones()
                }
            )
        }

        if (alVerContactos != null) {
            DropdownMenuItem(
                text = {
                    Text(
                        if (esSupervisor) {
                            "Consultar contactos"
                        } else {
                            "Gestionar contactos"
                        }
                    )
                },
                onClick = {
                    menuAbierto = false
                    alVerContactos()
                }
            )
        }

        if (alAbrirLectorQR != null) {
            DropdownMenuItem(
                text = {
                    Text("Leer código QR")
                },
                onClick = {
                    menuAbierto = false
                    alAbrirLectorQR()
                }
            )
        }

        DropdownMenuItem(
            text = {
                Text(
                    text = "Cerrar sesión",
                    color = MaterialTheme.colorScheme.error
                )
            },
            onClick = {
                menuAbierto = false
                alCerrarSesion()
            }
        )
    }
}