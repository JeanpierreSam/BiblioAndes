package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.model.ValidacionSolicitud
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * RF-03: registra un préstamo solo si cumple RN-01, RN-02 y RN-04.
 * La fecha límite sale de RN-03 (siete días).
 */
class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val proveedorFecha: ProveedorFecha
) {

    suspend operator fun invoke(libro: Libro): Result<Prestamo> = runCatching {
        val prestamos = obtenerPrestamos().getOrThrow()
        val validacion = ReglasPrestamo.validarSolicitud(libro, prestamos)
        if (validacion != ValidacionSolicitud.Permitida) {
            throw PrestamoRechazadoException(validacion)
        }
        val hoy = proveedorFecha.hoy()
        repository.registrarPrestamo(
            libroId = libro.id,
            fechaPrestamo = hoy.toString(),
            fechaLimite = ReglasPrestamo.calcularFechaLimite(hoy).toString()
        )
    }
}

/** El préstamo no se registró porque incumple una regla de negocio. */
class PrestamoRechazadoException(val motivo: ValidacionSolicitud) : Exception(motivo.mensaje)
