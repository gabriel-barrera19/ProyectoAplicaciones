package com.example.proyectoaplicaciones.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.data.local.AppDatabase
import com.example.proyectoaplicaciones.data.repository.OportunidadRepository
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import com.example.proyectoaplicaciones.ui.screens.ListaOportunidadesScreen
// Define las rutas de navegación de la aplicación.
object Rutas {
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
}

// Configura la navegación y las dependencias del módulo.
@Composable
fun AppNavigation() {

    // Controla los desplazamientos entre pantallas.
    val navController = rememberNavController()

    // Obtiene el contexto de Android.
    val context = LocalContext.current

    // Obtiene la instancia de la base de datos Room.
    val database = AppDatabase.obtenerInstancia(context)

    // Crea el repositorio que conecta el ViewModel con Room.
    val repository = OportunidadRepository(
        database.oportunidadDao()
    )

    // Crea una fábrica para proporcionar el repositorio al ViewModel.
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(
                    OportunidadViewModel::class.java
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return OportunidadViewModel(repository) as T
            }

            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }
    }

    // Crea el ViewModel con su repositorio.
    val oportunidadViewModel: OportunidadViewModel = viewModel(
        factory = factory
    )

    // Define las pantallas y sus rutas.
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO
    ) {

        // Primera pantalla: lista de oportunidades.
        composable(Rutas.INICIO) {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,
                alRegistrarOportunidad = {
                    navController.navigate(Rutas.FORMULARIO)
                }
            )
        }

        // Segunda pantalla: formulario de oportunidades.
        composable(Rutas.FORMULARIO) {
            OportunidadFormScreen(
                viewModel = oportunidadViewModel,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Pantalla inicial para acceder al formulario.
@Composable
private fun PantallaInicio(
    alAbrirFormulario: () -> Unit
) {
    Scaffold { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Gestión de Oportunidades")

            Button(
                onClick = alAbrirFormulario,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Registrar oportunidad")
            }
        }
    }
}