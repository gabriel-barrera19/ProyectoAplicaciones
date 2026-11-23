package com.example.proyectoaplicaciones.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.proyectoaplicaciones.repository.EventoRepository
import com.example.proyectoaplicaciones.ui.screens.EventoScreen
import com.example.proyectoaplicaciones.viewmodel.EventoViewModel

object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val INICIO = "inicio"
    const val FORMULARIO = "formulario"
    const val LECTOR_QR = "lector_qr"
    const val ORGANIZACIONES = "organizaciones"
    const val CONTACTOS = "contactos"
    const val SEGUIMIENTOS = "seguimientos"
    const val EVENTOS = "eventos"
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

    // Base de datos.
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

    val eventoRepository = EventoRepository(
        database.eventoDao()
    )

    // Fábrica de ViewModels.
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

            if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventoViewModel(
                    eventoRepository
                ) as T
            }

            throw IllegalArgumentException("ViewModel desconocido")
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

    val eventoViewModel: EventoViewModel = viewModel(
        key = "EventoViewModel",
        factory = factory
    )

    // CIERRE DE SESIÓN CENTRALIZADO.
    // La navegación se ejecuta mediante el efecto global de sesión.
    val cerrarSesion = {
        oportunidadSeleccionada = null
        usuarioAutenticado = null
    }

    // REDIRECCIÓN CENTRALIZADA CUANDO NO EXISTE UNA SESIÓN.
    // Evita navegar directamente durante la composición.
    LaunchedEffect(usuarioAutenticado) {

        if (usuarioAutenticado == null) {

            val rutaActual = navController.currentBackStackEntry
                ?.destination
                ?.route

            if (rutaActual != Rutas.LOGIN) {

                navController.navigate(Rutas.LOGIN) {
                    popUpTo(navController.graph.id) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // LOGIN.
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

        // MENÚ PRINCIPAL.
        composable(Rutas.MENU) {

            val usuario = usuarioAutenticado

            if (usuario != null) {

                MenuPrincipalScreen(
                    usuario = usuario,

                    alVerOportunidades = {
                        navController.navigate(Rutas.INICIO)
                    },

                    // Ejecutivo y Supervisor.
                    alVerOrganizaciones = {
                        if (
                            usuario.rol.equals("EJECUTIVO_COMERCIAL", true) ||
                            usuario.rol.equals("SUPERVISOR_COMERCIAL", true)
                        ) {
                            navController.navigate(Rutas.ORGANIZACIONES)
                        }
                    },

                    // Ejecutivo y Supervisor.
                    alVerContactos = {
                        if (
                            usuario.rol.equals("EJECUTIVO_COMERCIAL", true) ||
                            usuario.rol.equals("SUPERVISOR_COMERCIAL", true)
                        ) {
                            navController.navigate(Rutas.CONTACTOS)
                        }
                    },

                    alCerrarSesion = {
                        cerrarSesion()
                    },

                    alAbrirLectorQR = {
                        navController.navigate(Rutas.LECTOR_QR)
                    },
                    alVerEventos = {
                        navController.navigate(Rutas.EVENTOS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // LISTA DE OPORTUNIDADES.
        composable(Rutas.INICIO) {

            val usuario = usuarioAutenticado

            if (usuario != null) {

                ListaOportunidadesScreen(
                    viewModel = oportunidadViewModel,
                    usuario = usuario,

                    alRegistrarOportunidad = {
                        if (usuario.rol.equals("EJECUTIVO_COMERCIAL", true)) {
                            oportunidadViewModel.prepararNuevaOportunidad()
                            navController.navigate(Rutas.FORMULARIO)
                        }
                    },

                    alEditarOportunidad = { oportunidad ->
                        if (usuario.rol.equals("EJECUTIVO_COMERCIAL", true)) {
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
                            launchSingleTop = true
                        }
                    },

                    alCerrarSesion = {
                        cerrarSesion()
                    },

                    alVerOrganizaciones = {
                        navController.navigate(Rutas.ORGANIZACIONES) {
                            launchSingleTop = true
                        }
                    },

                    // Ambos perfiles pueden consultar contactos.
                    alVerContactos = {
                        if (
                            usuario.rol.equals("EJECUTIVO_COMERCIAL", true) ||
                            usuario.rol.equals("SUPERVISOR_COMERCIAL", true)
                        ) {
                            navController.navigate(Rutas.CONTACTOS) {
                                launchSingleTop = true
                            }
                        }
                    },

                    alAbrirLectorQR = {
                        navController.navigate(Rutas.LECTOR_QR) {
                            launchSingleTop = true
                        }
                    },
                    alVerEventos = {
                        navController.navigate(Rutas.EVENTOS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // FORMULARIO DE OPORTUNIDADES.
        // Exclusivo del Ejecutivo Comercial.
        composable(Rutas.FORMULARIO) {

            val usuario = usuarioAutenticado

            val tienePermiso = usuario?.rol
                ?.equals("EJECUTIVO_COMERCIAL", true) == true

            LaunchedEffect(usuario) {
                if (usuario != null && !tienePermiso) {
                    navController.popBackStack()
                }
            }

            if (usuario != null && tienePermiso) {

                OportunidadFormScreen(
                    viewModel = oportunidadViewModel,
                    eventoViewModel = eventoViewModel,
                    usuario = usuario,

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
                        cerrarSesion()
                    }
                )
            }
        }

        // SEGUIMIENTOS.
        composable(Rutas.SEGUIMIENTOS) {

            val usuario = usuarioAutenticado
            val oportunidad = oportunidadSeleccionada

            LaunchedEffect(usuario, oportunidad) {
                if (usuario != null && oportunidad == null) {
                    navController.popBackStack()
                }
            }

            if (usuario != null && oportunidad != null) {

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

        // LECTOR QR.
        composable(Rutas.LECTOR_QR) {

            QrScannerScreen(
                alVolver = {
                    navController.popBackStack()
                }
            )
        }

        // ORGANIZACIONES.
        // Disponible para Ejecutivo y Supervisor.
        composable(Rutas.ORGANIZACIONES) {

            val usuario = usuarioAutenticado

            val tienePermiso =
                usuario?.rol?.equals("EJECUTIVO_COMERCIAL", true) == true ||
                        usuario?.rol?.equals("SUPERVISOR_COMERCIAL", true) == true

            LaunchedEffect(usuario) {
                if (usuario != null && !tienePermiso) {
                    navController.popBackStack()
                }
            }

            if (usuario != null && tienePermiso) {

                OrganizacionScreen(
                    usuario = usuario,
                    viewModel = organizacionViewModel,

                    alVolver = {
                        navController.popBackStack()
                    },

                    alVerOportunidades = {
                        navController.navigate(Rutas.INICIO) {
                            launchSingleTop = true
                        }
                    },

                    // Ambos perfiles pueden consultar contactos.
                    alVerContactos = {
                        navController.navigate(Rutas.CONTACTOS) {
                            launchSingleTop = true
                        }
                    },

                    alAbrirLectorQR = {
                        navController.navigate(Rutas.LECTOR_QR) {
                            launchSingleTop = true
                        }
                    },

                    alCerrarSesion = {
                        cerrarSesion()
                    },
                    alVerEventos = {
                        navController.navigate(Rutas.EVENTOS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // CONTACTOS.
        // Disponible para Ejecutivo y Supervisor.
        composable(Rutas.CONTACTOS) {

            val usuario = usuarioAutenticado

            val tienePermiso =
                usuario?.rol?.equals("EJECUTIVO_COMERCIAL", true) == true ||
                        usuario?.rol?.equals("SUPERVISOR_COMERCIAL", true) == true

            LaunchedEffect(usuario) {
                if (usuario != null && !tienePermiso) {
                    navController.popBackStack()
                }
            }

            if (usuario != null && tienePermiso) {

                ContactoScreen(
                    contactoViewModel = contactoViewModel,
                    organizacionViewModel = organizacionViewModel,
                    usuario = usuario,

                    alVolver = {
                        navController.popBackStack()
                    },

                    alVerOportunidades = {
                        navController.navigate(Rutas.INICIO) {
                            launchSingleTop = true
                        }
                    },

                    alVerOrganizaciones = {
                        navController.navigate(Rutas.ORGANIZACIONES) {
                            launchSingleTop = true
                        }
                    },

                    alAbrirLectorQR = {
                        navController.navigate(Rutas.LECTOR_QR) {
                            launchSingleTop = true
                        }
                    },

                    alCerrarSesion = {
                        cerrarSesion()
                    },
                    alVerEventos = {
                        navController.navigate(Rutas.EVENTOS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
        // EVENTOS.
// Disponible para Ejecutivo y Supervisor.
        composable(Rutas.EVENTOS) {

            val usuario = usuarioAutenticado

            val tienePermiso =
                usuario?.rol?.equals("EJECUTIVO_COMERCIAL", true) == true ||
                        usuario?.rol?.equals("SUPERVISOR_COMERCIAL", true) == true

            LaunchedEffect(usuario) {
                if (usuario != null && !tienePermiso) {
                    navController.popBackStack()
                }
            }

            if (usuario != null && tienePermiso) {

                EventoScreen(
                    eventoViewModel = eventoViewModel,
                    organizacionViewModel = organizacionViewModel,
                    usuario = usuario,

                    alVolver = {
                        navController.popBackStack()
                    },

                    alVerOportunidades = {
                        navController.navigate(Rutas.INICIO) {
                            launchSingleTop = true
                        }
                    },

                    alVerOrganizaciones = {
                        navController.navigate(Rutas.ORGANIZACIONES) {
                            launchSingleTop = true
                        }
                    },

                    alVerContactos = {
                        navController.navigate(Rutas.CONTACTOS) {
                            launchSingleTop = true
                        }
                    },

                    alCerrarSesion = {
                        cerrarSesion()
                    }
                )
            }
        }
    }
}
