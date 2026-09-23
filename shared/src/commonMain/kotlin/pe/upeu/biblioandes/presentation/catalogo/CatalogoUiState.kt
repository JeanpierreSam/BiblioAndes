package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro

/** Estado completo del catálogo: filtros elegidos + fase de la lista. */
data class CatalogoUiState(
    val categorias: List<String> = emptyList(),
    /** null = "Todas". */
    val categoriaSeleccionada: String? = null,
    val consulta: String = "",
    val fase: FaseCatalogo = FaseCatalogo.Cargando
)

sealed interface FaseCatalogo {
    data object Cargando : FaseCatalogo
    data class Contenido(val libros: List<Libro>) : FaseCatalogo
    data object Vacio : FaseCatalogo
    data class Error(val mensaje: String) : FaseCatalogo
}
