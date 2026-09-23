package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/** RN-04: regulariza un préstamo registrando su devolución con la fecha de hoy. */
class DevolverPrestamoUseCase(
    private val repository: BibliotecaRepository,
    private val proveedorFecha: ProveedorFecha
) {

    suspend operator fun invoke(prestamoId: Int): Result<Unit> = runCatching {
        repository.registrarDevolucion(prestamoId, proveedorFecha.hoy().toString())
    }
}
