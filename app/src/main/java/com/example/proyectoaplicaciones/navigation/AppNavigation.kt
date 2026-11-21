package com.example.proyectoaplicaciones.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.data.local.AppDatabase
import com.example.proyectoaplicaciones.data.model.UsuarioResponse
import com.example.proyectoaplicaciones.repository.OportunidadRepository
import com.example.proyectoaplicaciones.ui.screens.ListaOportunidadesScreen
import com.example.proyectoaplicaciones.ui.screens.LoginScreen
import com.example.proyectoaplicaciones.ui.screens.MenuPrincipalScreen
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.ui.screens.QrScannerScreen
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

// Define las rutas de navegación.
object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
    const val LECTOR_QR = "lector_qr"
}

// Configura la navegación y las dependencias.
@Composable
fun AppNavigation() {

    // Controla la navegación entre pantallas.
    val navController = rememberNavController()

    // Conserva los datos del usuario autenticado durante la sesión.
    var usuarioAutenticado by remember {
        mutableStateOf<UsuarioResponse?>(null)
    }

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
            if (
                modelClass.isAssignableFrom(
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
                alLoginExitoso = { usuario ->

                    // Guarda los datos del usuario autenticado.
                    usuarioAutenticado = usuario

                    // Navega al menú principal.
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Menú principal después de iniciar sesión.
        composable(Rutas.MENU) {

            val usuario = usuarioAutenticado

            if (usuario == null) {

                // Impide acceder al menú sin autenticación.
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.MENU) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            } else {

                MenuPrincipalScreen(
                    usuario = usuario,

                    alVerOportunidades = {
                        navController.navigate(Rutas.INICIO)
                    },

                    alCerrarSesion = {

                        // Limpia los datos de la sesión.
                        usuarioAutenticado = null

                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },

                    alAbrirLectorQR = {
                        navController.navigate(Rutas.LECTOR_QR)
                    }
                )
            }
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

                // Regresa al menú principal.
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
                    usuarioAutenticado = null

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Formulario para registrar o editar una oportunidad.
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
                    usuarioAutenticado = null

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Pantalla del lector QR.
        composable(Rutas.LECTOR_QR) {

            QrScannerScreen(
                alVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}