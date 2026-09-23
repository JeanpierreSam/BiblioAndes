package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.model.normalizado

/**
 * RF-02 + RF-05: filtra por categoría (null = todas) y por texto en el título
 * o el autor, sin distinguir mayúsculas ni tildes.
 */
class FiltrarCatalogoUseCase {

    operator fun invoke(
        libros: List<Libro>,
        categoria: String?,
        consulta: String,
        soloDisponibles: Boolean = false
    ): List<Libro> {
        val texto = consulta.normalizado()
        return libros
            .filter { categoria == null || it.categoria == categoria }
            .filter { !soloDisponibles || ReglasPrestamo.tieneEjemplares(it) }
            .filter {
                texto.isEmpty() ||
                    it.titulo.normalizado().contains(texto) ||
                    it.autor.normalizado().contains(texto)
            }
    }
}
