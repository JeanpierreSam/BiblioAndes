package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.presentation.prestamos.TipoEstado

/** Píldora de texto con colores de fondo y de contenido. */
@Composable
fun Etiqueta(texto: String, fondo: Color, contenido: Color, modifier: Modifier = Modifier) {
    Surface(color = fondo, contentColor = contenido, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Etiqueta del estado de un préstamo; solo traduce el tipo a colores del tema. */
@Composable
fun EtiquetaEstado(tipo: TipoEstado, texto: String, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val (fondo, contenido) = when (tipo) {
        TipoEstado.ACTIVO -> colores.primaryContainer to colores.onPrimaryContainer
        TipoEstado.DEVUELTO -> colores.surfaceVariant to colores.onSurfaceVariant
        TipoEstado.VENCIDO -> colores.errorContainer to colores.onErrorContainer
    }
    Etiqueta(texto, fondo, contenido, modifier)
}

/**
 * Ejemplares de un libro. [disponible] llega calculado por el dominio
 * (ReglasPrestamo.tieneEjemplares); aquí solo se elige el color.
 */
@Composable
fun EtiquetaDisponibilidad(ejemplares: Int, disponible: Boolean, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    if (disponible) {
        Etiqueta("$ejemplares disp.", colores.tertiaryContainer, colores.onTertiaryContainer, modifier)
    } else {
        Etiqueta("Agotado", colores.errorContainer, colores.onErrorContainer, modifier)
    }
}
