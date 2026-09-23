package pe.upeu.biblioandes.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

/**
 * Las cuatro reglas de negocio de BiblioAndes en un solo lugar.
 * Ningún composable ni ViewModel repite estas condiciones: las consultan aquí.
 */
object ReglasPrestamo {

    /** RN-01: máximo de préstamos Activos simultáneos. */
    const val MAX_PRESTAMOS_ACTIVOS = 3

    /** RN-03: todo préstamo dura siete días. */
    const val DIAS_DE_PRESTAMO = 7

    /** RN-03: fecha límite de un préstamo que empieza en [fechaPrestamo]. */
    fun calcularFechaLimite(fechaPrestamo: LocalDate): LocalDate =
        fechaPrestamo.plus(DIAS_DE_PRESTAMO, DateTimeUnit.DAY)

    /**
     * RN-03: si la fecha límite ya pasó, el préstamo es Vencido; si no, Activo.
     * Un préstamo Devuelto conserva su estado.
     */
    fun estadoSegunFecha(prestamo: Prestamo, hoy: LocalDate): EstadoPrestamo {
        if (prestamo.estado is EstadoPrestamo.Devuelto) return prestamo.estado
        val dias = hoy.daysUntil(LocalDate.parse(prestamo.fechaLimite))
        return if (dias < 0) {
            EstadoPrestamo.Vencido(diasDeAtraso = -dias)
        } else {
            EstadoPrestamo.Activo(diasRestantes = dias)
        }
    }

    /** RN-02: un libro solo se puede prestar si le quedan ejemplares. */
    fun tieneEjemplares(libro: Libro): Boolean = libro.ejemplaresDisponibles > 0

    /** RN-01: cantidad de préstamos Activos (ya evaluados con RN-03). */
    fun contarActivos(prestamos: List<Prestamo>): Int =
        prestamos.count { it.estado is EstadoPrestamo.Activo }

    /** RN-01: true si el estudiante ya llegó al límite de préstamos activos. */
    fun alcanzoLimite(prestamos: List<Prestamo>): Boolean =
        contarActivos(prestamos) >= MAX_PRESTAMOS_ACTIVOS

    /** RN-04: true si tiene al menos un préstamo Vencido sin regularizar. */
    fun tieneVencidos(prestamos: List<Prestamo>): Boolean =
        prestamos.any { it.estado is EstadoPrestamo.Vencido }

    /**
     * Aplica RN-02, RN-04 y RN-01 (en ese orden) sobre préstamos ya evaluados
     * con RN-03 y devuelve el primer motivo que impide el préstamo.
     */
    fun validarSolicitud(libro: Libro, prestamos: List<Prestamo>): ValidacionSolicitud = when {
        !tieneEjemplares(libro) -> ValidacionSolicitud.SinEjemplares
        tieneVencidos(prestamos) -> ValidacionSolicitud.TienePrestamoVencido
        alcanzoLimite(prestamos) -> ValidacionSolicitud.LimiteAlcanzado
        else -> ValidacionSolicitud.Permitida
    }
}

/** Resultado de aplicar las reglas antes de registrar un préstamo. */
sealed class ValidacionSolicitud(val mensaje: String) {
    data object Permitida : ValidacionSolicitud("Puedes solicitar este libro")

    data object SinEjemplares :
        ValidacionSolicitud("No hay ejemplares disponibles de este libro")

    data object TienePrestamoVencido :
        ValidacionSolicitud("Tienes un préstamo vencido. Regularízalo antes de solicitar otro libro")

    data object LimiteAlcanzado :
        ValidacionSolicitud("Ya tienes ${ReglasPrestamo.MAX_PRESTAMOS_ACTIVOS} préstamos activos, el máximo permitido")
}
