package com.example.proyectoaplicaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectoaplicaciones.ui.navigation.AppNavigation
import com.example.proyectoaplicaciones.ui.theme.ProyectoAplicacionesTheme

/**
 * Actividad principal de la aplicación.
 *
 * Inicia Jetpack Compose y carga la navegación,
 * que conecta las pantallas del módulo de oportunidades.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite que la interfaz utilice el espacio
        // disponible de la pantalla.
        enableEdgeToEdge()

        // Inicia la interfaz con el tema de la aplicación.
        setContent {
            ProyectoAplicacionesTheme {

                // Carga las rutas y pantallas de la aplicación.
                AppNavigation()
            }
        }
    }
}