package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

/** Rutas de navegación como constantes: un solo lugar para escribirlas. */
object Rutas {
    const val INICIO = "inicio"
    const val CATALOGO = "catalogo"
    const val PRESTAMOS = "prestamos"
    const val PERFIL = "perfil"

    const val ARG_LIBRO_ID = "libroId"
    const val DETALLE = "detalle/{$ARG_LIBRO_ID}"

    fun detalle(libroId: Int) = "detalle/$libroId"
}

/** Los tres destinos de la barra inferior (RF-07). */
enum class DestinoInferior(val ruta: String, val etiqueta: String, val icono: ImageVector) {
    INICIO(Rutas.INICIO, "Inicio", Icons.Default.Home),
    CATALOGO(Rutas.CATALOGO, "Catálogo", Icons.AutoMirrored.Filled.MenuBook),
    PRESTAMOS(Rutas.PRESTAMOS, "Préstamos", Icons.Default.Bookmarks)
}
