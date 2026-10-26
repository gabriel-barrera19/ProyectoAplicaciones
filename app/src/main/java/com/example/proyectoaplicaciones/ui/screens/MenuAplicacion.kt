package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu

/**
 * Menú reutilizable para las pantallas internas.
 */
@Composable
fun MenuAplicacion(
    alIrMenuPrincipal: () -> Unit,
    alVerOportunidades: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    // Controla si el menú está abierto.
    var menuAbierto by remember { mutableStateOf(false) }

    // Botón hamburguesa de la barra superior.
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

    // Opciones disponibles al pulsar el botón.
    DropdownMenu(
        expanded = menuAbierto,
        onDismissRequest = {
            menuAbierto = false
        }
    ) {
        DropdownMenuItem(
            text = { Text("Menú principal") },
            onClick = {
                menuAbierto = false
                alIrMenuPrincipal()
            }
        )

        DropdownMenuItem(
            text = { Text("Gestión de oportunidades") },
            onClick = {
                menuAbierto = false
                alVerOportunidades()
            }
        )

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