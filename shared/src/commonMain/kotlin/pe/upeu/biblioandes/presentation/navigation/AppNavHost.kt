package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.catalogo.CatalogoRoute
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroRoute
import pe.upeu.biblioandes.presentation.inicio.InicioRoute
import pe.upeu.biblioandes.presentation.perfil.PerfilRoute
import pe.upeu.biblioandes.presentation.prestamos.PrestamosRoute

/**
 * Scaffold con la barra inferior y el grafo de navegación. La barra solo se
 * muestra en los tres destinos principales; Detalle y Perfil se apilan encima
 * y el botón atrás del sistema los cierra.
 */
@Composable
fun AppNavHost(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarBarra = DestinoInferior.entries.any { it.ruta == rutaActual }

    val barraViewModel: BarraInferiorViewModel = koinViewModel()
    val activos by barraViewModel.activos.collectAsStateWithLifecycle()
    // Se refresca cada vez que cambia la pantalla activa: un préstamo nuevo o
    // devuelto en Detalle/Préstamos debe reflejarse de inmediato en el badge.
    LaunchedEffect(rutaActual) { barraViewModel.actualizar() }

    Scaffold(
        // Cada pantalla tiene su propio Scaffold con TopAppBar y maneja sus márgenes.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(
                    rutaActual = rutaActual,
                    activos = activos,
                    onSeleccionar = { navController.navegarAPestana(it.ruta) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            composable(Rutas.INICIO) {
                InicioRoute(
                    onAbrirCatalogo = { navController.navegarAPestana(Rutas.CATALOGO) },
                    onAbrirPrestamos = { navController.navegarAPestana(Rutas.PRESTAMOS) },
                    onAbrirPerfil = { navController.navigate(Rutas.PERFIL) },
                    onAbrirLibro = { navController.navigate(Rutas.detalle(it)) }
                )
            }
            composable(Rutas.CATALOGO) {
                CatalogoRoute(onAbrirLibro = { navController.navigate(Rutas.detalle(it)) })
            }
            composable(Rutas.PRESTAMOS) {
                PrestamosRoute(onAbrirLibro = { navController.navigate(Rutas.detalle(it)) })
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument(Rutas.ARG_LIBRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val libroId = entrada.arguments?.read { getInt(Rutas.ARG_LIBRO_ID) } ?: 0
                DetalleLibroRoute(libroId = libroId, onVolver = { navController.navigateUp() })
            }
            composable(Rutas.PERFIL) {
                PerfilRoute(
                    modoOscuro = modoOscuro,
                    onModoOscuroChange = onModoOscuroChange,
                    onVolver = { navController.navigateUp() }
                )
            }
        }
    }
}

@Composable
private fun BarraInferior(rutaActual: String?, activos: Int, onSeleccionar: (DestinoInferior) -> Unit) {
    NavigationBar {
        DestinoInferior.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino.ruta == rutaActual,
                onClick = { onSeleccionar(destino) },
                icon = {
                    if (destino == DestinoInferior.PRESTAMOS && activos > 0) {
                        BadgedBox(badge = { Badge { Text("$activos") } }) {
                            Icon(destino.icono, contentDescription = null)
                        }
                    } else {
                        Icon(destino.icono, contentDescription = null)
                    }
                },
                label = { Text(destino.etiqueta) }
            )
        }
    }
}

/**
 * Cambia de pestaña sin apilar copias: vuelve al inicio del grafo guardando
 * el estado de la pestaña que se deja y restaurando el de la que se abre.
 */
fun NavHostController.navegarAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
