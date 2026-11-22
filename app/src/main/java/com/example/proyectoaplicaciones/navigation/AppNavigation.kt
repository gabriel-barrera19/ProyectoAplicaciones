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
import com.example.proyectoaplicaciones.model.UsuarioLocal
import com.example.proyectoaplicaciones.repository.ContactoRepository
import com.example.proyectoaplicaciones.repository.OportunidadRepository
import com.example.proyectoaplicaciones.repository.OrganizacionRepository
import com.example.proyectoaplicaciones.ui.screens.ContactoScreen
import com.example.proyectoaplicaciones.ui.screens.ListaOportunidadesScreen
import com.example.proyectoaplicaciones.ui.screens.LoginScreen
import com.example.proyectoaplicaciones.ui.screens.MenuPrincipalScreen
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.ui.screens.OrganizacionScreen
import com.example.proyectoaplicaciones.ui.screens.QrScannerScreen
import com.example.proyectoaplicaciones.viewmodel.ContactoViewModel
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import com.example.proyectoaplicaciones.viewmodel.OrganizacionViewModel

object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
    const val LECTOR_QR = "lector_qr"
    const val ORGANIZACIONES = "organizaciones"
    const val CONTACTOS = "contactos"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var usuarioAutenticado by remember {
        mutableStateOf<UsuarioLocal?>(null)
    }

    val context = LocalContext.current

    // Base de datos local.
    val database = AppDatabase.obtenerInstancia(context)

    // Repositorios.
    val oportunidadRepository = OportunidadRepository(
        database.oportunidadDao()
    )

    val organizacionRepository = OrganizacionRepository(
        database.organizacionDao()
    )

    val contactoRepository = ContactoRepository(
        database.contactoDao()
    )

    // Fábrica para crear los ViewModels.
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
                return OportunidadViewModel(
                    oportunidadRepository
                ) as T
            }

            if (
                modelClass.isAssignableFrom(
                    OrganizacionViewModel::class.java
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return OrganizacionViewModel(
                    organizacionRepository
                ) as T
            }

            if (
                modelClass.isAssignableFrom(
                    ContactoViewModel::class.java
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return ContactoViewModel(
                    contactoRepository
                ) as T
            }

            throw IllegalArgumentException(
                "ViewModel desconocido"
            )
        }
    }

    // ViewModels.
    val oportunidadViewModel: OportunidadViewModel = viewModel(
        key = "OportunidadViewModel",
        factory = factory
    )

    val organizacionViewModel: OrganizacionViewModel = viewModel(
        key = "OrganizacionViewModel",
        factory = factory
    )

    val contactoViewModel: ContactoViewModel = viewModel(
        key = "ContactoViewModel",
        factory = factory
    )

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // Inicio de sesión.
        composable(Rutas.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel()

            LoginScreen(
                viewModel = loginViewModel,
                alLoginExitoso = { usuario ->
                    usuarioAutenticado = usuario

                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Menú principal.
        composable(Rutas.MENU) {
            val usuario = usuarioAutenticado

            if (usuario == null) {

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

                    alVerOrganizaciones = {
                        navController.navigate(Rutas.ORGANIZACIONES)
                    },

                    alVerContactos = {
                        navController.navigate(Rutas.CONTACTOS)
                    },

                    alCerrarSesion = {
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

        // Lista de oportunidades.
        composable(Rutas.INICIO) {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,

                alRegistrarOportunidad = {
                    oportunidadViewModel.prepararNuevaOportunidad()
                    navController.navigate(Rutas.FORMULARIO)
                },

                alEditarOportunidad = { oportunidad ->
                    oportunidadViewModel.iniciarEdicion(oportunidad)
                    navController.navigate(Rutas.FORMULARIO)
                },

                alIrMenuPrincipal = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.MENU) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

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

        // Formulario de oportunidades.
        composable(Rutas.FORMULARIO) {
            OportunidadFormScreen(
                viewModel = oportunidadViewModel,

                alVolver = {
                    navController.popBackStack()
                },

                alIrMenuPrincipal = {
                    navController.navigate(Rutas.MENU) {
                        launchSingleTop = true
                    }
                },

                alVerOportunidades = {
                    navController.navigate(Rutas.INICIO) {
                        launchSingleTop = true
                    }
                },

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

        // Lector QR.
        composable(Rutas.LECTOR_QR) {
            QrScannerScreen(
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Módulo de organizaciones.
        composable(Rutas.ORGANIZACIONES) {
            OrganizacionScreen(
                viewModel = organizacionViewModel,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Módulo de contactos.
        composable(Rutas.CONTACTOS) {
            ContactoScreen(
                contactoViewModel = contactoViewModel,
                organizacionViewModel = organizacionViewModel,
                alVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}