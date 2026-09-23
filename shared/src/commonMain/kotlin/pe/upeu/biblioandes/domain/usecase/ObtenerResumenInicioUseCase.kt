package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * RF-01: estudiante + préstamo no devuelto cuya devolución vence primero.
 * Como la lista viene ordenada por fecha límite, un Vencido aparece antes
 * que cualquier Activo.
 */
class ObtenerResumenInicioUseCase(
    private val obtenerEstudiante: ObtenerEstudianteUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) {

    suspend operator fun invoke(): Result<ResumenInicio> = runCatching {
        coroutineScope {
            val estudiante = async { obtenerEstudiante().getOrThrow() }
            val prestamos = async { obtenerPrestamos().getOrThrow() }
            ResumenInicio(
                estudiante = estudiante.await(),
                proximoAVencer = prestamos.await().firstOrNull { it.estado !is EstadoPrestamo.Devuelto }
            )
        }
    }
}

/** proximoAVencer es null cuando el estudiante no tiene préstamos pendientes. */
data class ResumenInicio(val estudiante: Estudiante, val proximoAVencer: Prestamo?)
