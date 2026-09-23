package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * RF-04: préstamos del estudiante con el estado recalculado para hoy (RN-03),
 * ordenados por la fecha de devolución más próxima.
 */
class ObtenerPrestamosUseCase(
    private val repository: BibliotecaRepository,
    private val proveedorFecha: ProveedorFecha
) {

    suspend operator fun invoke(): Result<List<Prestamo>> = runCatching {
        val hoy = proveedorFecha.hoy()
        repository.obtenerPrestamos()
            .map { it.copy(estado = ReglasPrestamo.estadoSegunFecha(it, hoy)) }
            // Las fechas ISO (aaaa-mm-dd) se ordenan bien como texto.
            .sortedBy { it.fechaLimite }
    }
}
