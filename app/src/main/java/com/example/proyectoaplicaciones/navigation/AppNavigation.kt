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
import com.example.proyectoaplicaciones.data.local.OportunidadEntity
import com.example.proyectoaplicaciones.model.UsuarioLocal

import com.example.proyectoaplicaciones.repository.ContactoRepository
import com.example.proyectoaplicaciones.repository.OportunidadRepository
import com.example.proyectoaplicaciones.repository.OrganizacionRepository
import com.example.proyectoaplicaciones.repository.SeguimientoRepository

import com.example.proyectoaplicaciones.ui.screens.ContactoScreen
import com.example.proyectoaplicaciones.ui.screens.ListaOportunidadesScreen
import com.example.proyectoaplicaciones.ui.screens.LoginScreen
import com.example.proyectoaplicaciones.ui.screens.MenuPrincipalScreen
import com.example.proyectoaplicaciones.ui.screens.OportunidadFormScreen
import com.example.proyectoaplicaciones.ui.screens.OrganizacionScreen
import com.example.proyectoaplicaciones.ui.screens.QrScannerScreen
import com.example.proyectoaplicaciones.ui.screens.SeguimientoScreen

import com.example.proyectoaplicaciones.viewmodel.ContactoViewModel
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel
import com.example.proyectoaplicaciones.viewmodel.OrganizacionViewModel
import com.example.proyectoaplicaciones.viewmodel.SeguimientoViewModel


object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
    const val LECTOR_QR = "lector_qr"
    const val ORGANIZACIONES = "organizaciones"
    const val CONTACTOS = "contactos"
    const val SEGUIMIENTOS = "seguimientos"
}


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var usuarioAutenticado by remember {
        mutableStateOf<UsuarioLocal?>(null)
    }

    var oportunidadSeleccionada by remember {
        mutableStateOf<OportunidadEntity?>(null)
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

    val seguimientoRepository = SeguimientoRepository(
        database.seguimientoDao()
    )

    // Fábrica para crear los ViewModels.
    val factory = object : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            if (modelClass.isAssignableFrom(OportunidadViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OportunidadViewModel(
                    oportunidadRepository
                ) as T
            }

            if (modelClass.isAssignableFrom(OrganizacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OrganizacionViewModel(
                    organizacionRepository
                ) as T
            }

            if (modelClass.isAssignableFrom(ContactoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactoViewModel(
                    contactoRepository
                ) as T
            }

            if (modelClass.isAssignableFrom(SeguimientoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeguimientoViewModel(
                    seguimientoRepository
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

    val seguimientoViewModel: SeguimientoViewModel = viewModel(
        key = "SeguimientoViewModel",
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

                    // Ambos perfiles pueden consultar organizaciones.
                    alVerOrganizaciones = {
                        if (
                            usuario.rol.equals(
                                "EJECUTIVO_COMERCIAL",
                                ignoreCase = true
                            ) ||
                            usuario.rol.equals(
                                "SUPERVISOR_COMERCIAL",
                                ignoreCase = true
                            )
                        ) {
                            navController.navigate(Rutas.ORGANIZACIONES)
                        }
                    },

                    // Por ahora, contactos continúa disponible
                    // únicamente para el Ejecutivo.
                    alVerContactos = {
                        if (
                            usuario.rol.equals(
                                "EJECUTIVO_COMERCIAL",
                                ignoreCase = true
                            )
                        ) {
                            navController.navigate(Rutas.CONTACTOS)
                        }
                    },

                    alCerrarSesion = {
                        usuarioAutenticado = null
                        oportunidadSeleccionada = null

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
            val usuario = usuarioAutenticado

            if (usuario == null) {

                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.INICIO) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            } else {

                ListaOportunidadesScreen(
                    viewModel = oportunidadViewModel,
                    usuario = usuario,

                    alRegistrarOportunidad = {
                        if (
                            usuario.rol.equals(
                                "EJECUTIVO_COMERCIAL",
                                true
                            )
                        ) {
                            oportunidadViewModel.prepararNuevaOportunidad()
                            navController.navigate(Rutas.FORMULARIO)
                        }
                    },

                    alEditarOportunidad = { oportunidad ->
                        if (
                            usuario.rol.equals(
                                "EJECUTIVO_COMERCIAL",
                                true
                            )
                        ) {
                            oportunidadViewModel.iniciarEdicion(oportunidad)
                            navController.navigate(Rutas.FORMULARIO)
                        }
                    },

                    alVerSeguimientos = { oportunidad ->
                        oportunidadSeleccionada = oportunidad
                        seguimientoViewModel.cargarSeguimientos(oportunidad.id)
                        navController.navigate(Rutas.SEGUIMIENTOS)
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
                        oportunidadSeleccionada = null

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

        // Formulario de oportunidades.
        composable(Rutas.FORMULARIO) {
            val usuario = usuarioAutenticado

            if (
                usuario == null ||
                !usuario.rol.equals(
                    "EJECUTIVO_COMERCIAL",
                    true
                )
            ) {
                navController.popBackStack()
            } else {

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
                        oportunidadSeleccionada = null

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

        // Historial de seguimientos.
        composable(Rutas.SEGUIMIENTOS) {

            val usuario = usuarioAutenticado
            val oportunidad = oportunidadSeleccionada

            if (usuario == null || oportunidad == null) {

                navController.popBackStack()

            } else {

                SeguimientoScreen(
                    viewModel = seguimientoViewModel,
                    oportunidadId = oportunidad.id,
                    nombreOportunidad = oportunidad.nombre,
                    usuario = usuario,
                    alVolver = {
                        navController.popBackStack()
                    }
                )
            }
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
        // Ejecutivo y Supervisor pueden consultar.
        composable(Rutas.ORGANIZACIONES) {
            val usuario = usuarioAutenticado

            if (
                usuario == null ||
                (
                        !usuario.rol.equals(
                            "EJECUTIVO_COMERCIAL",
                            true
                        ) &&
                                !usuario.rol.equals(
                                    "SUPERVISOR_COMERCIAL",
                                    true
                                )
                        )
            ) {
                navController.popBackStack()

            } else {

                OrganizacionScreen(
                    usuario = usuario,
                    viewModel = organizacionViewModel,
                    alVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Módulo de contactos.
        // Temporalmente disponible solo para el Ejecutivo.
        composable(Rutas.CONTACTOS) {
            val usuario = usuarioAutenticado

            if (
                usuario == null ||
                !usuario.rol.equals(
                    "EJECUTIVO_COMERCIAL",
                    true
                )
            ) {
                navController.popBackStack()

            } else {

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
}