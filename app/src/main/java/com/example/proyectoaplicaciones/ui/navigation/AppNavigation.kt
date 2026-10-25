package com.example.proyectoaplicaciones.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.data.local.AppDatabase
import com.example.proyectoaplicaciones.data.repository.OportunidadRepository
import com.example.proyectoaplicaciones.ui.screens.ListaOportunidadesScreen
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

// Define las rutas de navegación de la aplicación.
object Rutas {
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
}

// Configura la navegación y las dependencias del módulo.
@Composable
fun AppNavigation() {

    // Controla la navegación entre las pantallas.
    val navController = rememberNavController()

    // Obtiene el contexto de Android.
    val context = LocalContext.current

    // Obtiene la instancia de la base de datos Room.
    val database = AppDatabase.obtenerInstancia(context)

    // Crea el repositorio que comunica el ViewModel con Room.
    val repository = OportunidadRepository(database.oportunidadDao())

    // Crea una fábrica para construir el ViewModel con el repositorio.
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OportunidadViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OportunidadViewModel(repository) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }

    // Comparte el mismo ViewModel entre la lista y el formulario.
    val oportunidadViewModel: OportunidadViewModel = viewModel(
        factory = factory
    )

    // Define las pantallas de la aplicación.
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO
    ) {

        // Pantalla principal con la lista de oportunidades.
        composable(Rutas.INICIO) {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,

                // Prepara un formulario vacío para registrar.
                alRegistrarOportunidad = {
                    oportunidadViewModel.prepararNuevaOportunidad()
                    navController.navigate(Rutas.FORMULARIO)
                },

                // Carga los datos de la oportunidad para editar.
                alEditarOportunidad = { oportunidad ->
                    oportunidadViewModel.iniciarEdicion(oportunidad)
                    navController.navigate(Rutas.FORMULARIO)
                }
            )
        }

        // Pantalla compartida para registrar y editar.
        composable(Rutas.FORMULARIO) {
            OportunidadFormScreen(
                viewModel = oportunidadViewModel,

                // Regresa a la lista al pulsar la flecha.
                alVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}