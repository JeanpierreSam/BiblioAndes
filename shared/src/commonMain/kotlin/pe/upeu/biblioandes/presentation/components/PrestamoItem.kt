package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.presentation.prestamos.PrestamoUi
import pe.upeu.biblioandes.presentation.prestamos.TipoEstado

/**
 * Tarjeta de un préstamo. Si [onDevolver] es null no se muestra el botón;
 * si [onClick] es null la tarjeta no es pulsable.
 */
@Composable
fun PrestamoItem(
    prestamo: PrestamoUi,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDevolver: (() -> Unit)? = null
) {
    val pulsable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    OutlinedCard(modifier = modifier.fillMaxWidth().then(pulsable)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = prestamo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                EtiquetaEstado(prestamo.tipo, prestamo.estadoTexto)
            }
            Text(prestamo.autor, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Prestado: ${prestamo.fechaPrestamo}  ·  Límite: ${prestamo.fechaLimite}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = prestamo.detalleEstado,
                style = MaterialTheme.typography.labelLarge,
                color = if (prestamo.tipo == TipoEstado.VENCIDO) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            if (onDevolver != null) {
                TextButton(onClick = onDevolver, modifier = Modifier.align(Alignment.End)) {
                    Text("Registrar devolución")
                }
            }
        }
    }
}
