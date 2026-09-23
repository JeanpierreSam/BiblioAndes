package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.ReglasPrestamo

/** RN-01 expuesta a la UI: cuántos activos hay y si ya se llegó al límite. */
class ObtenerCupoPrestamosUseCase(private val obtenerPrestamos: ObtenerPrestamosUseCase) {

    suspend operator fun invoke(): Result<CupoPrestamos> = obtenerPrestamos().map { prestamos ->
        CupoPrestamos(
            activos = ReglasPrestamo.contarActivos(prestamos),
            limiteAlcanzado = ReglasPrestamo.alcanzoLimite(prestamos)
        )
    }
}

data class CupoPrestamos(val activos: Int, val limiteAlcanzado: Boolean)
