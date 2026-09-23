package pe.upeu.biblioandes.presentation.detalle

import pe.upeu.biblioandes.domain.model.Libro

data class DetalleLibroUiState(
    val fase: FaseDetalle = FaseDetalle.Cargando,
    val mostrarConfirmacion: Boolean = false,
    val solicitando: Boolean = false,
    val limiteAlcanzado: Boolean = false,
    /** Resultado de la solicitud para el Snackbar; null cuando ya se mostró. */
    val mensaje: String? = null
)

sealed interface FaseDetalle {
    data object Cargando : FaseDetalle
    data class Contenido(val libro: Libro) : FaseDetalle
    data object NoEncontrado : FaseDetalle
}
