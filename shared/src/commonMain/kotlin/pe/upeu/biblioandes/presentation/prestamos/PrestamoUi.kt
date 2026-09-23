package pe.upeu.biblioandes.presentation.prestamos

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

/** Tono de la etiqueta de estado; la pantalla decide el color con él. */
enum class TipoEstado { ACTIVO, DEVUELTO, VENCIDO }

/** Préstamo listo para dibujar: textos armados y sin lógica de negocio. */
data class PrestamoUi(
    val id: Int,
    val libroId: Int,
    val titulo: String,
    val autor: String,
    val fechaPrestamo: String,
    val fechaLimite: String,
    val tipo: TipoEstado,
    val estadoTexto: String,
    val detalleEstado: String,
    val puedeDevolver: Boolean
)

/** Traduce el préstamo de dominio a textos de pantalla según su estado. */
fun Prestamo.aUi(): PrestamoUi {
    val (tipo, texto, detalle) = when (val e = estado) {
        is EstadoPrestamo.Activo -> Triple(
            TipoEstado.ACTIVO,
            "Activo",
            when (e.diasRestantes) {
                0 -> "Vence hoy"
                1 -> "Vence mañana"
                else -> "Vence en ${e.diasRestantes} días"
            }
        )
        is EstadoPrestamo.Devuelto -> Triple(
            TipoEstado.DEVUELTO,
            "Devuelto",
            "Devuelto el ${e.fechaDevolucion}"
        )
        is EstadoPrestamo.Vencido -> Triple(
            TipoEstado.VENCIDO,
            "Vencido",
            if (e.diasDeAtraso == 1) "1 día de atraso" else "${e.diasDeAtraso} días de atraso"
        )
    }
    return PrestamoUi(
        id = id,
        libroId = libro.id,
        titulo = libro.titulo,
        autor = libro.autor,
        fechaPrestamo = fechaPrestamo,
        fechaLimite = fechaLimite,
        tipo = tipo,
        estadoTexto = texto,
        detalleEstado = detalle,
        puedeDevolver = estado !is EstadoPrestamo.Devuelto
    )
}
