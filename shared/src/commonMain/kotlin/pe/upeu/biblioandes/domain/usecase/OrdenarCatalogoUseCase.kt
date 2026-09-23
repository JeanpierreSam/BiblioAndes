package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.CriterioOrden
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.normalizado

/** Ordena el catálogo ya filtrado, sin decidir el filtro. */
class OrdenarCatalogoUseCase {

    operator fun invoke(libros: List<Libro>, criterio: CriterioOrden): List<Libro> = when (criterio) {
        CriterioOrden.TITULO -> libros.sortedBy { it.titulo.normalizado() } // "Álgebra" junto a la "A"
        CriterioOrden.ANIO -> libros.sortedByDescending { it.anio } // más recientes primero
    }
}
