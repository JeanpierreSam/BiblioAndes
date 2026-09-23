package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/** RF-03: un libro por su id. */
class ObtenerLibroUseCase(private val repository: BibliotecaRepository) {

    suspend operator fun invoke(id: Int): Result<Libro> = runCatching {
        repository.obtenerLibro(id) ?: throw NoSuchElementException("El libro no existe")
    }
}
