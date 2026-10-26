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
import com.example.proyectoaplicaciones.ui.screens.LoginScreen
import com.example.proyectoaplicaciones.ui.screens.MenuPrincipalScreen
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

// Define las rutas de navegación.
object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
}

// Configura la navegación y las dependencias.
@Composable
fun AppNavigation() {

    // Controla la navegación entre pantallas.
    val navController = rememberNavController()

    // Obtiene el contexto de Android.
    val context = LocalContext.current

    // Obtiene la base de datos local.
    val database = AppDatabase.obtenerInstancia(context)

    // Crea el repositorio de oportunidades.
    val repository = OportunidadRepository(database.oportunidadDao())

    // Permite crear el ViewModel con su repositorio.
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

            throw IllegalArgumentException("ViewModel desconocido")
        }
    }

    // Comparte el ViewModel entre la lista y el formulario.
    val oportunidadViewModel: OportunidadViewModel = viewModel(
        factory = factory
    )

    // Define las pantallas de la aplicación.
    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // Pantalla de inicio de sesión.
        composable(Rutas.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel()

            LoginScreen(
                viewModel = loginViewModel,
                alLoginExitoso = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Menú principal después de iniciar sesión.
        composable(Rutas.MENU) {
            MenuPrincipalScreen(
                alVerOportunidades = {
                    navController.navigate(Rutas.INICIO)
                },
                alCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MENU) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Lista de oportunidades comerciales.
        composable(Rutas.INICIO) {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,

                // Abre el formulario para registrar una oportunidad.
                alRegistrarOportunidad = {
                    oportunidadViewModel.prepararNuevaOportunidad()
                    navController.navigate(Rutas.FORMULARIO)
                },

                // Abre el formulario para editar una oportunidad.
                alEditarOportunidad = { oportunidad ->
                    oportunidadViewModel.iniciarEdicion(oportunidad)
                    navController.navigate(Rutas.FORMULARIO)
                },

                // Regresa al menú principal sin duplicarlo en la navegación.
                alIrMenuPrincipal = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.MENU) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                // Cierra la sesión y limpia las pantallas anteriores.
                alCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Formulario para registrar o editar.
        composable(Rutas.FORMULARIO) {
            OportunidadFormScreen(
                viewModel = oportunidadViewModel,

                // Regresa a la pantalla anterior.
                alVolver = {
                    navController.popBackStack()
                },

                // Navega al menú principal.
                alIrMenuPrincipal = {
                    navController.navigate(Rutas.MENU) {
                        launchSingleTop = true
                    }
                },

                // Navega a la lista de oportunidades.
                alVerOportunidades = {
                    navController.navigate(Rutas.INICIO) {
                        launchSingleTop = true
                    }
                },

                // Cierra la sesión y vuelve al Login.
                alCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}